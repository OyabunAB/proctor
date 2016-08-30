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
package se.oyabun.proctor.proxy.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * Proctor Netty Channel Initializer
 */
@Component
public class ProctorChannelInitializer
        extends ChannelInitializer<SocketChannel> {

    private static final Logger logger = LoggerFactory.getLogger(ProctorChannelInitializer.class);

    private final ProctorHttpHandler proctorHttpHandler;

    private SslContext sslContext = null;

    private final String keystorePath;

    private final String keyStorePassword;

    @Autowired
    public ProctorChannelInitializer(final ProctorHttpHandler proctorHttpHandler,
                                     @Value("${se.oyabun.proctor.proxy.local.keystore.path:#{null}}")
                                     final String keystorePath,
                                     @Value("${se.oyabun.proctor.proxy.local.keystore.password:#{null}}")
                                     final String keyStorePassword) {

        this.proctorHttpHandler = proctorHttpHandler;

        this.keystorePath = keystorePath;

        this.keyStorePassword = keyStorePassword;

    }

    @Override
    public void initChannel(SocketChannel channel) {

        try {

            if (StringUtils.isNotBlank(keystorePath) && StringUtils.isNotBlank(keyStorePassword)) {

                SelfSignedCertificate ssc = new SelfSignedCertificate();

                sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();

            }

        } catch (CertificateException e) {

            logger.error("Failed to initialize SSL Certificate.", e );

        } catch (SSLException e) {

            logger.error("Failed to initialize SSL.", e );

        }

        ChannelPipeline pipeline = channel.pipeline();

        if (sslContext != null) {

            pipeline.addLast(sslContext.newHandler(channel.alloc()));

        }

        pipeline.addLast("timeout", new ReadTimeoutHandler(15));

        pipeline.addLast("codec-http", new HttpServerCodec());

        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));

        pipeline.addLast("proctor", proctorHttpHandler);

    }

}