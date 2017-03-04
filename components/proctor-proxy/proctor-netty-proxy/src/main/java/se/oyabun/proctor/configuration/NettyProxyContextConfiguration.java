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

import io.netty.handler.ssl.SslContextBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.oyabun.proctor.proxy.netty.OptionalSslContext;

import javax.net.ssl.KeyManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@Configuration
public class NettyProxyContextConfiguration {

    private static final Logger log = LoggerFactory.getLogger(NettyProxyContextConfiguration.class);

    @Value("${se.oyabun.proctor.proxy.local.keystore.path:#{null}}")
    private String keystorePath;

    @Value("${se.oyabun.proctor.proxy.local.keystore.password:#{null}}")
    private String keyStorePassword;

    @Bean
    public OptionalSslContext optionalSslContext()
            throws KeyStoreException,
                   NoSuchAlgorithmException,
                   UnrecoverableKeyException,
                   IOException,
                   CertificateException {

        if (StringUtils.isNotBlank(keystorePath) &&
            StringUtils.isNotBlank(keyStorePassword)) {

            if(log.isInfoEnabled()) {

                log.info("Initializing TLS with keystore properties.");

            }

            KeyStore keyStore = KeyStore.getInstance("JKS");

            keyStore.load(new FileInputStream(keystorePath),
                          keyStorePassword.toCharArray());

            //
            // Set up key manager factory to use our key store
            //
            KeyManagerFactory keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

            keyManagerFactory.init(keyStore,
                                   keyStorePassword.toCharArray());

            return new OptionalSslContext(
                    SslContextBuilder.forServer(keyManagerFactory)
                                     .build());

        } else {

            return new OptionalSslContext();

        }

    }

}
