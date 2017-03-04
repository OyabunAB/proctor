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
package se.oyabun.proctor.web.admin.security.oauth2.access.v1;

import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.oyabun.proctor.security.SecurityUtil;
import se.oyabun.proctor.security.Token;

/**
 * Proctor Token REST API
 *
 * @version 1
 */
@RestController
@RequestMapping(AccessRestController.ACCESS_ROOT)
public class AccessRestController {

    private static final Logger log = LoggerFactory.getLogger(AccessRestController.class);

    public static final String ACCESS_ROOT = "/security/v1/access/";
    public static final String ACCESS_TOKENS = "/tokens";

    @Value("${se.oyabun.proctor.security.signingKey}")
    private String signingKey;

    @RequestMapping(value = ACCESS_TOKENS,
                    method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Token> exchange(@RequestBody
                                          final Token grantsToken) {

        if(log.isTraceEnabled()) {

            log.trace("Received authorization token: '{}'.", grantsToken.getSignedContent());

        }


        final String username = SecurityUtil.getUserNameForAccessToken(grantsToken.getSignedContent(),
                                                                       signingKey);

        final boolean grantedAdmin = SecurityUtil.isUserAdministrator(grantsToken.getSignedContent(),
                                                                      signingKey);

        try {

            final Token accessToken = SecurityUtil.generateAccessToken(username,
                                                                       grantedAdmin,
                                                                       signingKey);

            return ResponseEntity.status(HttpStatus.OK).body(accessToken);

        } catch (JwtException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

        }




    }

}
