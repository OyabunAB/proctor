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
package se.oyabun.proctor.proxy.litteproxy.filters;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Proctor handlers enabled filter source for LittleProxy
 */
@Component
public class ProctorHandlersFilterSource
        extends HttpFiltersSourceAdapter {

    private final Logger logger = LoggerFactory.getLogger(ProctorHandlersFilterSource.class);

    @Autowired
    private ProctorHandlersHTTPFilter proctorHandlersHTTPFilter;

    @Override
    public HttpFilters filterRequest(HttpRequest httpRequest,
                                     ChannelHandlerContext channelHandlerContext) {

        if(logger.isDebugEnabled()) {

            logger.debug("Filtering request '{} {}'.",
                    httpRequest.getMethod(),
                    httpRequest.getUri());

        }

        return proctorHandlersHTTPFilter;

    }

}
