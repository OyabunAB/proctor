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
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.proxy.AbstractProctorProxy;
import se.oyabun.proctor.proxy.ProctorProxy;

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

    private ProctorChannelInitializer proctorChannelInitializer;

    private Channel channel;
    private EventLoopGroup masterGroup = new NioEventLoopGroup();
    private EventLoopGroup slaveGroup = new NioEventLoopGroup();


    @Autowired
    public ProctorNettyProxy(final ProctorChannelInitializer proctorChannelInitializer) {

        this.proctorChannelInitializer = proctorChannelInitializer;

    }

    /**
     * ${@inheritDoc}
     */
    public void startProxy()
            throws
            Exception {

        if (logger.isDebugEnabled()) {

            logger.debug("Starting Proctor Netty HTTP Server.");

        }

        channel = new ServerBootstrap()
                .group(masterGroup,
                       slaveGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(proctorChannelInitializer)
                .bind(proxyListenAddress,
                      proxyListenPort)
                .channel();

    }

    /**
     * ${@inheritDoc}
     */
    public void stopProxy() {

        if (logger.isDebugEnabled()) {

            logger.debug("Shutting down Proctor Netty HTTP Server.");

        }

        channel.close();

        masterGroup.shutdownGracefully();

        slaveGroup.shutdownGracefully();

    }

}
