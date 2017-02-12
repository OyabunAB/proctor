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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import se.oyabun.proctor.ProctorServerConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Proctor admin service context properties
 */
@Configuration
@EnableSwagger2
@ComponentScan("se.oyabun.proctor.web")
public class ProctorAdminWebServiceContextConfiguration {

    @Bean
    public ApiInfo apiInfo() {

        return new ApiInfoBuilder().title("Proctor Open Proxy Framework REST API")
                                   .description("Proctor framework REST API v1 documentation.")
                                   .termsOfServiceUrl("")
                                   .license("Copyright 2016 Oyabun AB, Licensed under the Apache License, Version 2.0")
                                   .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                                   .version("0.0.1-SNAPSHOT")
                                   .build();
    }

    @Autowired
    @Bean
    public Docket usersDocket(final ApiInfo apiInfo,
                              final ProctorServerConfiguration configuration) {

        return new Docket(DocumentationType.SWAGGER_2).groupName("proxy")
                                                      .apiInfo(apiInfo)
                                                      .host(configuration.getProxyAddressAndPort())
                                                      .select()
                                                      .paths(input -> input.contains("/api/1/clusters") ||
                                                                      input.contains("/api/1/handlers") ||
                                                                      input.contains("/api/1/statistics"))
                                                      .build();
    }

}
