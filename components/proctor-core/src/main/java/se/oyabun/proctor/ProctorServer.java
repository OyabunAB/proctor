package se.oyabun.proctor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.FileNotFoundException;

/**
 * Proctor Server
 */
@Configuration
@ComponentScan("se.oyabun.proctor.configuration")
public class ProctorServer {

    public static final String DEFAULT_PROCTOR_PROPERTIES_FILE = "./proctor.properties";

    public static final String PROCTOR_PROPERTY_FILE_PROPERTY = "proctor.properties";

    /**
     * Set up external properties file, checking for system properties override, then default.
     * @return configured property placeholderconfigurer
     * @throws FileNotFoundException if no properties file can be found.
     */
    @Bean
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() throws FileNotFoundException {

        final String propertiesFileOverride = System.getProperty(PROCTOR_PROPERTY_FILE_PROPERTY);

        final FileSystemResource defaultPropertiesFile = new FileSystemResource(DEFAULT_PROCTOR_PROPERTIES_FILE);

        PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();

        if(StringUtils.isNotBlank(propertiesFileOverride)) {

            propertyPlaceholderConfigurer.setLocation(new FileSystemResource(propertiesFileOverride));

        } else if(defaultPropertiesFile.exists()) {

            propertyPlaceholderConfigurer.setLocation(defaultPropertiesFile);

        } else {

            throw new FileNotFoundException("No properties file was found.");

        }


        return propertyPlaceholderConfigurer;

    }

}
