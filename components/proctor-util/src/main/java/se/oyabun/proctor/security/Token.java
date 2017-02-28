package se.oyabun.proctor.security;

import java.io.Serializable;

public class Token
        implements Serializable {

    private String username;

    private String signedContent;

    private String expires;

    private Type type;

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public Type getType() {

        return type;
    }

    public void setType(Type type) {

        this.type = type;
    }

    public String getSignedContent() {

        return signedContent;
    }

    public void setSignedContent(String signedContent) {

        this.signedContent = signedContent;
    }

    public String getExpires() {

        return expires;
    }

    public void setExpires(String expires) {

        this.expires = expires;

    }

    public enum Type {

        AUTHORIZATION, ACCESS

    }

}
