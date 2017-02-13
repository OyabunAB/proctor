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
package se.oyabun.proctor.handler.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.exceptions.NoHandleForNameException;
import se.oyabun.proctor.exceptions.URIParseException;
import se.oyabun.proctor.handler.ProctorRouteHandler;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Proctor Zookeeper enabled route handler
 *
 * @author Daniel Sundberg
 * @author Johan Maasing
 */
@Component
public class ProctorZookeeperRouteHandler
        implements ProctorRouteHandler {

    private static final Pattern SERVICE_LOCATOR_PATTERN = Pattern.compile("^/([^/]+)/(.*)");
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<String, CuratorFramework> curatorCache = new HashMap<>();
    private final Map<String, ServiceDiscovery<Void>> serviceDiscoveryCache = new HashMap<>();
    private final Map<String, ServiceProvider<Void>> serviceProviderCache = new HashMap<>();

    /**
     * ${@inheritDoc}
     */
    @Override
    public URL resolveURLFor(final String uri,
                             final ProctorHandlerConfiguration properties)
            throws
            NoHandleForNameException,
            MalformedURLException {

        try {

            final ParseResult parseURI = parseURI(uri);

            final Optional<ServiceProvider<Void>> optionalServiceProvider = cacheLoadServiceProvider(parseURI.getServiceName(),
                                                                                                     properties);

            if (optionalServiceProvider.isPresent()) {

                final ServiceProvider<Void> serviceProvider = optionalServiceProvider.get();

                return new URL(new URL(serviceProvider.getInstance()
                                                      .buildUriSpec()),
                               parseURI.getUri());

            } else {

                throw new NoHandleForNameException("Handle could not be found for handle name.");

            }

        } catch (Exception e) {

            if (log.isDebugEnabled()) {

                log.debug("Failed to parse given URI.");

            }

            throw new MalformedURLException("Could not generate URL for handle name and URI.");

        }

    }

    /**
     * Parse a given URI for service name and request uri
     *
     * @param uri to parse
     * @return wrapped parsed result
     * @throws URIParseException if parsing fails or does not conform to pattern
     */
    public static ParseResult parseURI(final String uri)
            throws
            URIParseException {

        if (uri == null) {

            throw new URIParseException("URI may not be null.");

        }

        final Matcher matcher = SERVICE_LOCATOR_PATTERN.matcher(uri);

        if (!matcher.matches()) {

            throw new URIParseException("URI: '" + uri + "'" + " does not match service locator pattern.");

        }

        // FIXME: can't happen if uri matches the current pattern
        if (matcher.groupCount() != 2) {

            throw new URIParseException("URI: '" + uri + "'" + " has the wrong number of capturing groups.");
        }

        final ParseResult result = new ParseResult("/" + matcher.group(1),
                                                   "/" + matcher.group(2));

        // FIXME: can't happen if uri matches the current pattern
        if (result.getServiceName() == null ||
            result.getServiceName()
                  .length() < 1) {

            throw new URIParseException("Parsed service name is empty or null.");

        }

        // FIXME: can't happen if uri matches (previous check) the current pattern
        if (uri.equals("/")) {

            throw new URIParseException("Parsed URI is empty or null.");

        }

        return result;

    }

    private Optional<ServiceProvider<Void>> cacheLoadServiceProvider(final String serviceName,
                                                                     final ProctorHandlerConfiguration properties) {

        Optional<ServiceProvider<Void>> optionalServiceProvider = Optional.ofNullable(serviceProviderCache.get
                (properties.getConfigurationID()));

        ServiceProvider<Void> serviceProvider = optionalServiceProvider.isPresent() ?
                                                optionalServiceProvider.get() :
                                                cacheLoadServiceDiscovery(properties).orElseThrow
                                                        (IllegalStateException::new)
                                                                                     .serviceProviderBuilder()
                                                                                     .serviceName(serviceName)
                                                                                     .build();

        if (optionalServiceProvider.isPresent()) {

            return optionalServiceProvider;

        } else {

            try {

                serviceProvider.start();

            } catch (Exception e) {

                return Optional.empty();

            }

            serviceProviderCache.put(properties.getConfigurationID(),
                                     serviceProvider);

            return Optional.of(serviceProvider);

        }

    }

    private Optional<ServiceDiscovery<Void>> cacheLoadServiceDiscovery(final ProctorHandlerConfiguration properties) {

        Optional<ServiceDiscovery<Void>> optionalServiceDiscovery = Optional.ofNullable(serviceDiscoveryCache.get
                (properties));

        ServiceDiscovery<Void> serviceDiscovery = optionalServiceDiscovery.isPresent() ?
                                                  optionalServiceDiscovery.get() :
                                                  ServiceDiscoveryBuilder.builder(Void.class)
                                                                         .client(cacheLoadCuratorFramework
                                                                                         (properties).orElseThrow
                                                                                 (IllegalStateException::new))
                                                                         .watchInstances(Boolean.valueOf(properties
                                                                                                                 .getProperties()
                                                                                                                   .getOrDefault("watchinstances",
                                                                                                                                 "true")))
                                                                         .basePath(properties.getProperties()
                                                                                             .getOrDefault("basepath",
                                                                                                           "/"))
                                                                         .build();

        if (optionalServiceDiscovery.isPresent()) {

            return optionalServiceDiscovery;

        } else {

            try {

                serviceDiscovery.start();

            } catch (Exception e) {

                return Optional.empty();

            }

            serviceDiscoveryCache.put(properties.getConfigurationID(),
                                      serviceDiscovery);

            return Optional.of(serviceDiscovery);

        }


    }

    private Optional<CuratorFramework> cacheLoadCuratorFramework(final ProctorHandlerConfiguration properties) {

        CuratorFramework curatorFramework = Optional.ofNullable(curatorCache.get(properties))
                                                    .orElseGet(() -> CuratorFrameworkFactory.newClient(properties
                                                                                                               .getProperties()
                                                                                                                 .getOrDefault("connectstring",
                                                                                                                               ""),
                                                                                                       new ExponentialBackoffRetry(Integer.parseInt(properties.getProperties()
                                                                                                                                                              .getOrDefault("backbasesleeptime",
                                                                                                                                                                            "10")),
                                                                                                                                   Integer.parseInt(properties.getProperties()
                                                                                                                                                              .getOrDefault("maxretries",
                                                                                                                                                                            "10")))));

        if (!curatorFramework.getState()
                             .equals(CuratorFrameworkState.STARTED)) {

            curatorFramework.start();

        }

        return Optional.of(curatorFramework);

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
