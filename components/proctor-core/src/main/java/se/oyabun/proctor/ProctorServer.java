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
package se.oyabun.proctor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * Proctor Proxy Server configuration
 */
@Configuration
@ComponentScan("se.oyabun.proctor.configuration")
public class ProctorServer {

    public static final String DEFAULT_PROCTOR_PROPERTIES_FILE = "./proctor.properties";

    public static final String APPLICATION_PROPERTY_FILE = "application.properties";

    public static final String PROCTOR_PROPERTY_FILE_PROPERTY = "proctor.properties";

    public static final String PROCTOR_PROPERTIES_ENVIRONMENT_VARIABLE = "PROCTOR_PROPERTIES";

    /**
     * Set up external properties file, checking for system properties override, then default.
     *
     * @return configured property placeholderconfigurer
     * @throws FileNotFoundException if no properties file can be found.
     */
    @Bean
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer()
            throws
            FileNotFoundException {

        final Optional<String> environmentPropertiesLocation =
                Optional.ofNullable(System.getenv(PROCTOR_PROPERTIES_ENVIRONMENT_VARIABLE));

        final String propertiesFileOverride = System.getProperty(PROCTOR_PROPERTY_FILE_PROPERTY);

        final FileSystemResource defaultProperties = new FileSystemResource(DEFAULT_PROCTOR_PROPERTIES_FILE);

        final FileSystemResource propertiesFile =
                new FileSystemResource(environmentPropertiesLocation.isPresent() ?
                                       environmentPropertiesLocation.get() : DEFAULT_PROCTOR_PROPERTIES_FILE);

        PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();

        Collection<Resource> properties = Arrays.asList(new ClassPathResource(APPLICATION_PROPERTY_FILE));

        if (StringUtils.isNotBlank(propertiesFileOverride)) {

            properties.add(new FileSystemResource(propertiesFileOverride));

        } else if (propertiesFile.exists()) {

            properties.add(propertiesFile);

        } else if(defaultProperties.exists()) {

            properties.add(defaultProperties);

        } else {

            throw new FileNotFoundException("Proctor properties file not configured, " +
                                            "override with system property 'proctor.properties', " +
                                            "or by setting environment variable 'PROCTOR_CONFIG'.");

        }

        propertyPlaceholderConfigurer.setLocations(
                properties.stream().toArray(Resource[]::new));

        return propertyPlaceholderConfigurer;

    }


}
