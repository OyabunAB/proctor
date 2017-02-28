package se.oyabun.proctor.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * No operation authentication manager
 */
public class PassthroughAuthenticationManager
        implements AuthenticationManager {

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        return authentication;

    }

}
