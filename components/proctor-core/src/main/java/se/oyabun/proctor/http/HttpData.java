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
package se.oyabun.proctor.http;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * HTTP Data wrapper
 */
public class HttpData
        implements Serializable {

    private final Map<String, Collection<String>> headers;
    private final byte[] body;

    public HttpData(final Map<String, Collection<String>> headers,
                    final byte[] body) {

        this.headers = headers;
        this.body = body;
    }

    @Override
    public int hashCode() {

        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.headers);
        hash = 37 * hash + Objects.hashCode(this.body);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {

            return false;

        }

        if (getClass() != obj.getClass()) {

            return false;

        }

        final HttpData other = (HttpData) obj;

        if (!Objects.equals(this.getHeaders(),
                            other.getHeaders())) {

            return false;
        }

        if (!Objects.equals(this.getBody(),
                            other.getBody())) {

            return false;

        }

        return true;

    }

    public Map<String, Collection<String>> getHeaders() {

        return headers;
    }

    public byte[] getBody() {

        return body;
    }

    @Override
    public String toString() {

        return "HttpData{" + "headers=" + headers + ", " + "body=" + body + '}';

    }

}