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
package se.oyabun.proctor.web.admin.security.oauth2.authorization.v1;

import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.oyabun.proctor.security.SecurityUtil;
import se.oyabun.proctor.security.Token;
import se.oyabun.proctor.security.UserIdentification;

/**
 * Proctor Authorization REST API
 *
 * @version 1
 */
@RestController
@RequestMapping(AuthorizationRestController.AUTHORIZATION_ROOT)
public class AuthorizationRestController {

    public static final Logger log = LoggerFactory.getLogger(AuthorizationRestController.class);

    public static final String AUTHORIZATION_ROOT = "/security/v1/authorization";
    public static final String AUTHORIZATION_TOKENS = "/tokens";

    @Value("${se.oyabun.proctor.security.signingKey}")
    private String signingKey;

    @RequestMapping(value = AUTHORIZATION_TOKENS,
                    method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<Token> authorize(@RequestBody
                                                         final UserIdentification identification) {

        try {

            final Token token = SecurityUtil.generateAuthorizationGrant(identification.getUsername(),
                                                                       true,
                                                                       signingKey);

            if(log.isTraceEnabled()) {

                log.trace("Generated authorization token: '{}'.", token.getSignedContent());

            }

            return ResponseEntity.status(HttpStatus.OK).body(token);

        } catch (JwtException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        }

    }

}
