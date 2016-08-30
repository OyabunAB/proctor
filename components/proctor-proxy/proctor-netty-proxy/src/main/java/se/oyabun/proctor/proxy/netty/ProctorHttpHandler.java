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

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Proctor Netty Http Handler
 */
@Component @ChannelHandler.Sharable
public class ProctorHttpHandler
        extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger logger = LoggerFactory.getLogger(ProctorHttpHandler.class);

    private ProctorHttpResponder proctorHttpResponder;

    @Autowired
    public ProctorHttpHandler(final ProctorHttpResponder proctorHttpResponder) {

        this.proctorHttpResponder = proctorHttpResponder;

    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx,
                             final FullHttpRequest request)
            throws Exception {

        ctx.writeAndFlush(proctorHttpResponder.processRequest(request));

    }

}