package se.oyabun.proctor.security;


public class AuthenticationTokenParserException
        extends RuntimeException {

    public AuthenticationTokenParserException(final String message) {

        super(message);
    }

    public AuthenticationTokenParserException(final String message,
                                              final Exception e) {

        super(message, e);

    }

}
