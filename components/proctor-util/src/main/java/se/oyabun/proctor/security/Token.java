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
