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

import io.jsonwebtoken.*;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

/**
 * Authentication token for JWT header
 */
public class AuthenticationToken
        extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 1L;

    private final Object principal;

    private Object details;

    Collection<GrantedAuthority> authorities = new HashSet<>();

    public AuthenticationToken(final String accessToken,
                               final String signingKey) {

        super(null);

        try {


            Jws<Claims> jws = SecurityUtil.parseClaims(accessToken, signingKey);

            this.principal = jws.getBody().get(SecurityUtil.USER_NAME_CLAIM_KEY);

            this.setDetailsAuthorities(jws);

            this.setAuthenticated(true);

        } catch (UnsupportedJwtException e) {

            throw new AuthenticationTokenParserException("Token is unsupported: " + e.getMessage());

        } catch (MalformedJwtException e) {

            throw new AuthenticationTokenParserException("Token is malformed: " + e.getMessage());

        } catch (SignatureException e) {

            throw new AuthenticationTokenParserException("Token signature not valid: " + e.getMessage());

        } catch (IllegalArgumentException e) {

            throw new AuthenticationTokenParserException("Token is illegal: " + e.getMessage());

        }

    }

    @Override
    public Object getCredentials() {

        return "";

    }

    @Override
    public Object getPrincipal() {

        return principal;

    }

    private void setDetailsAuthorities(Jws<Claims> jws) {

        details = jws;

        if(Boolean.TRUE.equals(jws.getBody().get(SecurityUtil.USER_ROLE_ADMIN_CLAIM_KEY))) {

            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        }

    }

    @Override
    public Collection getAuthorities() {

        return authorities;

    }

}