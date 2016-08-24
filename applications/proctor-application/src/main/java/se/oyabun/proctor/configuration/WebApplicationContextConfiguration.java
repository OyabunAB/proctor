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

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Embedded web application context configuration
 */
@Configuration
@ComponentScan("se.oyabun.proctor.web")
public class WebApplicationContextConfiguration
        implements EmbeddedServletContainerCustomizer {

    @Bean
    public EmbeddedServletContainerFactory embeddedServletContainerFactory() {

        return new JettyEmbeddedServletContainerFactory();

    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer servletContainer) {

        servletContainer.setContextPath("/proctoradmin");

    }

}
