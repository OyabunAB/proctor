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
package se.oyabun.proctor.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Custom token authentication filter,
 */
public class CustomTokenAuthenticationFilter
        extends AbstractAuthenticationProcessingFilter {

    private static final Logger logger = LoggerFactory.getLogger(CustomTokenAuthenticationFilter.class);

    private String signingKey;

    public CustomTokenAuthenticationFilter(final String defaultFilterProcessesUrl,
                                           final String signingKey) {

        super(defaultFilterProcessesUrl);

        super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(defaultFilterProcessesUrl));

        setAuthenticationManager(new PassthroughAuthenticationManager());

        setAuthenticationSuccessHandler(new TokenSimpleUrlAuthenticationSuccessHandler());

        this.signingKey = signingKey;

    }


    public final String HEADER_SECURITY_TOKEN = HttpHeaders.AUTHORIZATION;




    /**
     * Attempt to authenticate request - basically just pass over to
     * another method to authenticate request headers
     *
     * @param request servletrequest
     * @param response serverlresponse
     * @return authentication
     * @throws AuthenticationException on exception
     * @throws IOException on exception
     * @throws ServletException on exception
     */
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        if(request.getHeader(HEADER_SECURITY_TOKEN) != null) {

            String token = request.getHeader(HEADER_SECURITY_TOKEN)!=null?
                    request.getHeader(HEADER_SECURITY_TOKEN).replace("Bearer ",""):
                    "";

            if(logger.isTraceEnabled()) {

                logger.trace("Found token: {}.", token);

            }

            return authUserByToken(token);

        }


        return null;

    }


    /**
     * Authenticates request token, or returns null
     *
     * @param token to authenticate
     * @return null or authentication token
     * @throws AuthenticationException if failure
     */
    private AbstractAuthenticationToken authUserByToken(String token)
            throws AuthenticationException {

        if(token == null) {

            return null;

        }

        try {

            return new AuthenticationToken(token, signingKey);

        } catch (ExpiredJwtException e) {

            if(logger.isTraceEnabled()) {

                logger.trace("Expired token, not authenticated.");

            }

            throw new CredentialsExpiredException("Expired.", e);

        }

    }


    /**
     * Authenticates via token in header
     *
     * @param servletRequest of request
     * @param servletResponse of response
     * @param chain of filters
     * @throws IOException when exception
     * @throws ServletException when exception
     */
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if(this.requiresAuthentication(request, response)) {

            Authentication authResult;

            try {

                authResult = this.attemptAuthentication(request, response);

                SecurityContextHolder.getContext().setAuthentication(authResult);

            } catch (InternalAuthenticationServiceException e) {

                this.logger.error("An internal error occurred while trying to authenticate.", e);

                this.unsuccessfulAuthentication(request, response, e);

                return;

            } catch (AuthenticationException e) {

                this.unsuccessfulAuthentication(request, response, e);

                return;

            }

        }

        chain.doFilter(request, response);

    }

}
