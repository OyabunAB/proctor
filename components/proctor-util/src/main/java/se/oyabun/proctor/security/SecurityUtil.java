package se.oyabun.proctor.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Proctor security util
 */
public class SecurityUtil {

    public static final String USER_NAME_CLAIM_KEY = "username";
    public static final String USER_ROLE_ADMIN_CLAIM_KEY = "ROLE_ADMIN";

    public static final String ACCESS_CLAIM_KEY = "access";

    private SecurityUtil() {}

    /**
     * Generate an authorization grant token
     *
     * @param username
     * @param isAdministrator
     * @param signingKey
     * @return generated json web token
     */
    public static Token generateAuthorizationGrant(final String username,
                                                   final boolean isAdministrator,
                                                   final String signingKey) {

        //
        // Populate token claims
        //
        final Map<String, Object> claims = new HashMap<>();
        claims.put(USER_NAME_CLAIM_KEY, username);
        claims.put(USER_ROLE_ADMIN_CLAIM_KEY, isAdministrator);

        final Token token = new Token();
        final DateTime issueTime = DateTime.now();
        final DateTime expirationTime = issueTime.plusDays(1);

        final String signedContent =
                Jwts.builder()
                    .setId(UUID.randomUUID().toString())
                    .setSubject(username)
                    .setClaims(claims)
                    .setIssuedAt(issueTime.toDate())
                    .setExpiration(expirationTime.toDate())
                    .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encode(signingKey.getBytes()))
                    .compact();

        token.setUsername(username);
        token.setType(Token.Type.AUTHORIZATION);
        token.setSignedContent(signedContent);
        token.setExpires(ISODateTimeFormat.dateTime().print(expirationTime));

        return token;

    }


    public static Token generateAccessToken(final String username,
                                            final boolean grantedAdmin,
                                            final String signingKey) {

        //
        // Populate token claims
        //
        final Map<String, Object> claims = new HashMap<>();
        claims.put(USER_NAME_CLAIM_KEY, username);
        claims.put(USER_ROLE_ADMIN_CLAIM_KEY, grantedAdmin);
        claims.put(ACCESS_CLAIM_KEY, "Look sir. Maybe they serve seafood.");

        final Token token = new Token();
        final DateTime issueTime = DateTime.now();
        final DateTime expirationTime = issueTime.plusMinutes(5);

        final String signedContent =
                Jwts.builder()
                    .setId(UUID.randomUUID().toString())
                    .setSubject(username)
                    .setClaims(claims)
                    .setIssuedAt(issueTime.toDate())
                    .setExpiration(expirationTime.toDate())
                    .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encode(signingKey.getBytes()))
                    .compact();

        token.setUsername(username);
        token.setType(Token.Type.ACCESS);
        token.setSignedContent(signedContent);
        token.setExpires(ISODateTimeFormat.dateTime().print(expirationTime));

        return token;


    }

    /**
     * Parse access token for user name
     *
     * @param signedToken to parse
     * @param signingKey for parsing
     * return user name claim in token
     */
    public static String getUserNameForAccessToken(final String signedToken,
                                                   final String signingKey) {


        return Jwts
                .parser()
                .setSigningKey(Base64.getEncoder().encode(signingKey.getBytes()))
                .parseClaimsJws(signedToken)
                .getBody().get(USER_NAME_CLAIM_KEY, String.class);

    }

    /**
     * Parse access token for administrator claim
     *
     * @param signedToken to parse
     * @param signingKey for parsing
     * @return true if token contains a claim for access token
     */
    public static boolean isUserAdministrator(final String signedToken,
                                              final String signingKey) {

        return Jwts
                .parser()
                .setSigningKey(Base64.getEncoder().encode(signingKey.getBytes()))
                .parseClaimsJws(signedToken)
                .getBody().get(USER_ROLE_ADMIN_CLAIM_KEY, Boolean.class);

    }

    public static Jws<Claims> parseClaims(String accessToken,
                                          String signingKey) {

        return Jwts
                .parser()
                .setSigningKey(Base64.getEncoder().encode(signingKey.getBytes()))
                .parseClaimsJws(accessToken);
    }
}
