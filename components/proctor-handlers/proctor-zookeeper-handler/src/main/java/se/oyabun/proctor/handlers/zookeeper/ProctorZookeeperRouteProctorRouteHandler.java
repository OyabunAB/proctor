/*
 * Copyright 2016 Oyabun AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.oyabun.proctor.handlers.zookeeper;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.exceptions.InputNotMatchedException;
import se.oyabun.proctor.exceptions.NoHandleForNameException;
import se.oyabun.proctor.exceptions.URIParseException;
import se.oyabun.proctor.handlers.ProctorRouteHandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Proctor Zookeeper enabled route handler
 * @author Daniel Sundberg
 * @author Johan Maasing
 */
@Component
public class ProctorZookeeperRouteProctorRouteHandler
        implements ProctorRouteHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProctorRouteHandler.class);

    private static final Pattern SERVICE_LOCATOR_PATTERN = Pattern.compile("^/([^/]+)/(.*)");

    private static final ConcurrentHashMap<String, ServiceProvider<Void>> PROVIDER_CACHE = new ConcurrentHashMap<>();

    @Autowired
    private ServiceDiscovery serviceDiscovery;

    /**
     * ${@inheritDoc}
     */
    public boolean matches(String uri) {

        try {

            final ParseResult parseURI = parseURI(uri);

            final Optional<ServiceProvider<Void>> optionalServiceProvider =
                    cacheLoadServiceProvider(parseURI.getServiceName());

            if(optionalServiceProvider.isPresent()) {

                return true;

            }

        } catch (URIParseException e) {

            if(logger.isDebugEnabled()) {

                logger.debug("Failed to parse given URI.");

            }

        }

        return false;

    }

    /**
     * ${@inheritDoc}
     */
    public String getHandleNameFor(String uri) throws InputNotMatchedException {

        try {

            final ParseResult parseURI = parseURI(uri);

            final Optional<ServiceProvider<Void>> optionalServiceProvider =
                    cacheLoadServiceProvider(parseURI.getServiceName());

            if(optionalServiceProvider.isPresent()) {

                return parseURI.getServiceName();

            } else {

                throw new InputNotMatchedException("Handle name not present for matching uri.");

            }

        } catch (URIParseException e) {

            if(logger.isDebugEnabled()) {

                logger.debug("Failed to parse given URI.");

            }

            throw new InputNotMatchedException("Could not get handle name due to failed uri parse attempt.");

        }


    }

    /**
     * ${@inheritDoc}
     */
    public Set<String> getHandleNames() {

        return PROVIDER_CACHE.keySet().stream().collect(Collectors.toSet());

    }

    /**
     * ${@inheritDoc}
     */
    public URL resolveURLFor(final String handleName,
                             final String uri)
            throws NoHandleForNameException, MalformedURLException {

        try {

            final ParseResult parseURI = parseURI(uri);

            assert(parseURI.getServiceName().equals(handleName));

            final Optional<ServiceProvider<Void>> optionalServiceProvider =
                    cacheLoadServiceProvider(handleName);

            if(optionalServiceProvider.isPresent()) {

                final ServiceProvider<Void> serviceProvider = optionalServiceProvider.get();

                return new URL(
                        new URL(serviceProvider.getInstance().buildUriSpec()),
                        parseURI.getUri());

            } else {

                throw new NoHandleForNameException("Handle could not be found for handle name.");

            }

        } catch (Exception e) {

            if(logger.isDebugEnabled()) {

                logger.debug("Failed to parse given URI.");

            }

            throw new MalformedURLException("Could not generate URL for handle name and URI.");

        }

    }

    /**
     * Get service provider for given service name.
     * Will add non cached service provider to cache if first call.
     * @param serviceName to search for
     * @return cached service provider
     */
    private Optional<ServiceProvider<Void>> cacheLoadServiceProvider(String serviceName) {

        ServiceProvider<Void> serviceProvider = PROVIDER_CACHE.get(serviceName);

        if (serviceProvider == null) {

            serviceProvider =
                    serviceDiscovery
                            .serviceProviderBuilder()
                            .serviceName(serviceName)
                            .build();

            try {

                serviceProvider.start();

            } catch (Exception e) {

                logger.error("Failed to start found service provider for '{}', ignoring.", serviceName);

                return Optional.empty();

            }

            //
            // Cache service provider for subsequent calls
            //
            PROVIDER_CACHE.put(serviceName, serviceProvider);

        }

        return Optional.of(serviceProvider);

    }

    /**
     * Parse a given URI for service name and request uri
     * @param uri to parse
     * @return wrapped parsed result
     * @throws URIParseException if parsing fails or does not conform to pattern
     */
    public static ParseResult parseURI(final String uri)
            throws URIParseException {

        if (uri == null) {

            throw new URIParseException("URI may not be null.") ;

        }

        final Matcher matcher = SERVICE_LOCATOR_PATTERN.matcher(uri);

        if (!matcher.matches()) {

            throw new URIParseException("URI: '" + uri + "'" +
                    " does not match service locator pattern.");

        }

        if (matcher.groupCount() != 2) {

            throw new URIParseException("URI: '" + uri + "'" +
                    " has the wrong number of capturing groups.");
        }

        final ParseResult result =
                new ParseResult(
                        "/" + matcher.group(1),
                        "/" + matcher.group(2));

        if (result.getServiceName() == null || result.getServiceName().length() < 1) {

            throw new URIParseException("Parsed service name is empty or null.");

        }

        if (uri == "/") {

            throw new URIParseException("Parsed URI is empty or null.");

        }

        return result;

    }

    /**
     * Convenience class for parsing results
     */
    public static class ParseResult {

        private String serviceName;

        private String uri;

        public ParseResult(final String serviceName,
                           final String uri) {

            this.serviceName = serviceName;

            this.uri = uri;

        }

        public String getServiceName() {

            return serviceName;

        }

        public String getUri() {

            return uri;

        }

    }

}
