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
     * Attempt to authenticate request - basically just pass over to another method to authenticate request headers
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
     * Authwnticates request token, or returns null
     * @return null or authentication token
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
     * @param servletRequest
     * @param servletResponse
     * @param chain
     * @throws IOException
     * @throws ServletException
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
