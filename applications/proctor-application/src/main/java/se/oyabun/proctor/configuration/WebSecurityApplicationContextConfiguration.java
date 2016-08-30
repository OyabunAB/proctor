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

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import java.util.Arrays;

/**
 * Web security context configuration
 */
@Configuration
@EnableAuthorizationServer
@EnableResourceServer
public class WebSecurityApplicationContextConfiguration {

    @Bean
    public static SecurityProperties securityProperties() {
        SecurityProperties securityProperties = new SecurityProperties();
        securityProperties.setIgnored(
                Arrays.asList(
                        "/assets/**",
                        "/administration/**",
                        "/webjars/**",
                        "/index.html",
                        "/"));
        return securityProperties;
    }


}
