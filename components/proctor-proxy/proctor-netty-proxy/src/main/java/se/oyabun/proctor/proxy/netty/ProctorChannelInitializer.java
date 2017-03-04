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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ProctorChannelInitializer
        extends ChannelInitializer<SocketChannel> {

    public static final String SSL_HANDLER = "tls";
    public static final String HTTP_HANDLER = "codec-http";
    public static final String HTTP_AGGREGATE_HANDLER = "aggregator";
    public static final String HTTP_PROXY_HANDLER = "proxy";

    private ProctorHttpHandler proctorHttpHandler;
    private OptionalSslContext optionalSslContext;

    @Autowired
    public ProctorChannelInitializer(final ProctorHttpHandler proctorHttpHandler,
                                     final OptionalSslContext optionalSslContext) {

        this.optionalSslContext = optionalSslContext;
        this.proctorHttpHandler = proctorHttpHandler;

    }

    @Override
    public void initChannel(final SocketChannel channel) {

        ChannelPipeline pipeline = channel.pipeline();


        optionalSslContext.ifPresent(
                sslContext -> pipeline.addLast(SSL_HANDLER,
                                               sslContext.newHandler(channel.alloc())));

        pipeline.addLast(HTTP_HANDLER,
                         new HttpServerCodec());


        pipeline.addLast(HTTP_AGGREGATE_HANDLER,
                         new HttpObjectAggregator(Integer.MAX_VALUE));


        pipeline.addLast(HTTP_PROXY_HANDLER,
                         proctorHttpHandler);

    }

}
