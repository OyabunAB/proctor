package se.oyabun.proctor.security;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

/**
 * DIB Authentication token
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