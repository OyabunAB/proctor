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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.proxy.AbstractProctorProxy;
import se.oyabun.proctor.proxy.ProctorProxy;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Proctor Netty Proxy Server Implementation
 */
@Component
public class ProctorNettyProxy
        extends AbstractProctorProxy
        implements ProctorProxy {

    private static final Logger logger = LoggerFactory.getLogger(ProctorNettyProxy.class);

    @Value("${se.oyabun.proctor.proxy.listen.port}")
    private int proxyListenPort;

    @Value("${se.oyabun.proctor.proxy.listen.address}")
    private String proxyListenAddress;

    @Value("${se.oyabun.proctor.proxy.local.keystore.path:#{null}}")
    private String keystorePath;

    @Value("${se.oyabun.proctor.proxy.local.keystore.password:#{null}}")
    private String keyStorePassword;

    @Autowired
    private ProctorHttpHandler proctorHttpHandler;

        private SslContext sslContext = null;

    private Channel channel;

    private EventLoopGroup masterGroup = new NioEventLoopGroup(1);
    private EventLoopGroup slaveGroup = new NioEventLoopGroup();

    /**
     * ${@inheritDoc}
     */
    public void startProxy() throws Exception {

        if(logger.isDebugEnabled()) {

            logger.debug("Starting Proctor Netty HTTP Server.");

        }

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(masterGroup, slaveGroup);

        serverBootstrap.channel(NioServerSocketChannel.class);

        serverBootstrap.handler(new LoggingHandler(LogLevel.ERROR));

        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel channel) {

                try {

                    if (StringUtils.isNotBlank(keystorePath) && StringUtils.isNotBlank(keyStorePassword)) {

                        KeyStore ks = KeyStore.getInstance("JKS");

                        ks.load(new FileInputStream(keystorePath), keyStorePassword.toCharArray());

                        //
                        // Set up key manager factory to use our key store
                        //
                        KeyManagerFactory keyManagerFactory =
                                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

                        keyManagerFactory.init(ks, keyStorePassword.toCharArray());

                        sslContext = SslContextBuilder.forServer(keyManagerFactory).build();

                    }

                } catch (CertificateException e) {

                    logger.error("Failed to initialize SSL Certificate.", e );

                } catch (SSLException e) {

                    logger.error("Failed to initialize SSL.", e );

                } catch (NoSuchAlgorithmException e) {

                    logger.error("Failed to initialize SSL.", e );

                } catch (KeyStoreException e) {

                    logger.error("Failed to initialize SSL.", e );

                } catch (IOException e) {

                    logger.error("Failed to initialize SSL.", e );

                } catch (UnrecoverableKeyException e) {

                    logger.error("Failed to initialize SSL.", e );

                }

                ChannelPipeline pipeline = channel.pipeline();

                if (sslContext != null) {

                    pipeline.addLast("tls", sslContext.newHandler(channel.alloc()));

                }

                pipeline.addLast("codec-http", new HttpServerCodec());

                pipeline.addLast("aggregator", new HttpObjectAggregator(65536));

                pipeline.addLast("proxy", proctorHttpHandler);

            }

        });

        channel = serverBootstrap.bind(proxyListenAddress, proxyListenPort).channel();

    }

    /**
     * ${@inheritDoc}
     */
    public void stopProxy() {

        if(logger.isDebugEnabled()) {

            logger.debug("Shutting down Proctor Netty HTTP Server.");

        }

        channel.close();

        masterGroup.shutdownGracefully();

        slaveGroup.shutdownGracefully();

    }

}
