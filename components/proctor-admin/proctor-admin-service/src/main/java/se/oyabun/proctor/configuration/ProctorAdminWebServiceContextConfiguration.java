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
package se.oyabun.proctor.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import se.oyabun.proctor.ProctorServerConfiguration;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;
import se.oyabun.proctor.handler.properties.ProctorRouteConfiguration;
import se.oyabun.proctor.security.CustomTokenAuthenticationFilter;
import se.oyabun.proctor.security.PassthroughAuthenticationManager;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Proctor admin service context properties
 */
@Configuration
@EnableSwagger2
@ComponentScan("se.oyabun.proctor.web")
public class ProctorAdminWebServiceContextConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ProctorAdminWebServiceContextConfiguration.class);

    @Value("${se.oyabun.proctor.security.signingKey}")
    private String signingKey;

    @Autowired
    private ProctorServerConfiguration localConfig;

    @Bean
    public ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                .title("Proctor Open Proxy Framework REST API")
                .description("Proctor framework REST API v1 documentation.")
                .termsOfServiceUrl("")
                .license("Copyright 2016 Oyabun AB, Licensed under the Apache License, Version 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .version("0.0.1-SNAPSHOT")
                .build();
    }

    @Bean
    @Autowired
    public Docket securityDocket(final ApiInfo apiInfo,
                                 final ProctorServerConfiguration configuration) {

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("security")
                .apiInfo(apiInfo)
                .host(configuration.getProxyAddressAndPort())
                .select()
                .paths(input -> input.startsWith("/security/"))
                .build();
    }

    @Bean
    @Autowired
    public Docket proxyDocket(final ApiInfo apiInfo,
                              final ProctorServerConfiguration configuration) {

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("api")
                .apiInfo(apiInfo)
                .host(configuration.getProxyAddressAndPort())
                .securitySchemes(Arrays.asList(new ApiKey(HttpHeaders.AUTHORIZATION,
                                                          "Bearer",
                                                          "header")))
                .select()
                .paths(input -> input.startsWith("/api/"))
                .build();

    }

    @Autowired
    public void configeJackson(final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder,
                               final ProctorHandlerConfigrationDeserializer proctorHandlerConfigrationDeserializer) {

        jackson2ObjectMapperBuilder.dateFormat(new ISO8601DateFormat());
        jackson2ObjectMapperBuilder.deserializerByType(ProctorHandlerConfiguration.class,
                                                       proctorHandlerConfigrationDeserializer);

    }


    /**
     * Set up specific route rule for this specific node (statistics)
     *
     * @return route configuration for this nodes statistics
     */
    @Bean
    public ProctorRouteConfiguration staticNodeSpecificStatisticsRoute() {

        return new ProctorRouteConfiguration(UUID.randomUUID().toString(),
                                             0,
                                             "(?<requestPath>" + localConfig.getContext() +
                                                     "/api/v1/cluster/nodes/" + localConfig.getNodeID() + "/.*)",
                                             "se.oyabun.proctor.handler.staticroute.ProctorStaticRouteHandler",
                                             false,
                                             ImmutableMap.of(ProctorRouteConfiguration.ROOT_URL_PROPERTY,
                                                             localConfig.getLocalContextUrl(),
                                                             ProctorRouteConfiguration.APPEND_PATH_PROPERTY,
                                                             "true",
                                                             ProctorRouteConfiguration.APPEND_MATCHER_GROUP,
                                                             "requestPath"));

    }

    /**
     * Set up the static route to the administration GUI,
     * will only be added once over cluster
     *
     * @return proctor route configuration for shared admin web route
     */
    @Bean
    public ProctorRouteConfiguration staticAdminWebRoute() {

        return new ProctorRouteConfiguration(UUID.randomUUID().toString(),
                                             1,
                                             "(?<requestPath>" + localConfig.getContext() + ".*)",
                                             "se.oyabun.proctor.handler.staticroute.ProctorStaticRouteHandler",
                                             false,
                                             ImmutableMap.of(ProctorRouteConfiguration.ROOT_URL_PROPERTY,
                                                             localConfig.getLocalContextUrl(),
                                                             ProctorRouteConfiguration.APPEND_PATH_PROPERTY,
                                                             "true",
                                                             ProctorRouteConfiguration.APPEND_MATCHER_GROUP,
                                                             "requestPath"));

    }

    /**
     * Set up the static route to the administration GUI,
     * will only be added once over cluster
     *
     * @return proctor route configuration for shared admin web route
     */
    @Bean
    public ProctorRouteConfiguration oyabunTestRoute() {

        return new ProctorRouteConfiguration(UUID.randomUUID().toString(),
                                             0,
                                             "/oyabun(?<requestPath>.*)",
                                             "se.oyabun.proctor.handler.staticroute.ProctorStaticRouteHandler",
                                             false,
                                             ImmutableMap.of(ProctorRouteConfiguration.ROOT_URL_PROPERTY,
                                                             "https://www.oyabun.se",
                                                             ProctorRouteConfiguration.APPEND_PATH_PROPERTY,
                                                             "true",
                                                             ProctorRouteConfiguration.APPEND_MATCHER_GROUP,
                                                             "requestPath"));

    }

    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    protected class ProctorWebSecurityConfig
            extends WebSecurityConfigurerAdapter {

        @Override
        @Autowired
        protected void configure(final AuthenticationManagerBuilder auth)
                throws Exception {

            auth.parentAuthenticationManager(passthroughAuthenticationManager());

        }

        @Override
        protected void configure(final HttpSecurity http)
                throws Exception {

            http
                    .authorizeRequests()
                        .antMatchers("/security/**").permitAll()
                        .antMatchers("/api/**").authenticated()
                    .and()
                        .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                    .and()
                        .anonymous()
                    .and()
                        .securityContext()
                    .and()
                        .headers().disable()
                        .rememberMe().disable()
                        .requestCache().disable()
                        .x509().disable()
                        .csrf().disable()
                        .httpBasic().disable()
                        .formLogin().disable()
                        .logout().disable()
                    .addFilterBefore(
                            new CustomTokenAuthenticationFilter("/api/**", signingKey),
                            AnonymousAuthenticationFilter.class)
                    .addFilterBefore(corsFilter(),
                                     CustomTokenAuthenticationFilter.class)
                    .exceptionHandling()
                    .authenticationEntryPoint(new Http403ForbiddenEntryPoint());

        }



        @Bean
        public CorsFilter corsFilter() {

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            config.addAllowedOrigin("*");
            config.addAllowedHeader("*");
            config.addAllowedMethod("*");
            source.registerCorsConfiguration("/**", config);

            return new CorsFilter(source);

        }

        @Bean
        public PassthroughAuthenticationManager passthroughAuthenticationManager() {

            return new PassthroughAuthenticationManager();

        }

    }


    @Component
    class ProctorHandlerConfigrationDeserializer
            extends JsonDeserializer<ProctorHandlerConfiguration> {


        @Override
        public ProctorHandlerConfiguration deserialize(JsonParser jsonParser,
                                                       DeserializationContext deserializationContext)
                throws IOException,
                       JsonProcessingException {

            ObjectCodec objectCodec = jsonParser.getCodec();
            JsonNode configuration = objectCodec.readTree(jsonParser);

            final String configurationID = configuration.get("configurationID").asText();
            final Integer priority = configuration.get("priority").asInt();
            final String pattern = configuration.get("pattern").asText();
            final String routeType = configuration.get("handlerType").asText();
            final Boolean persistent = configuration.get("persistent").asBoolean();
            JsonNode properties = configuration.get("properties");
            final Map<String,String> propertiesMap = new HashMap<>();
            properties.fieldNames().forEachRemaining(propertyName -> {
                propertiesMap.put(propertyName,properties.get(propertyName).asText());
            });

            return new ProctorRouteConfiguration(configurationID,
                                                 priority,
                                                 pattern,
                                                 routeType,
                                                 persistent,
                                                 propertiesMap);

        }

    }

}
