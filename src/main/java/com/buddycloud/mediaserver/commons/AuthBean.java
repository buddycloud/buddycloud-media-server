/*
 * Copyright 2014 Buddycloud
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.buddycloud.mediaserver.commons;

import org.apache.commons.codec.binary.Base64;
import org.restlet.Request;
import org.restlet.data.ChallengeResponse;

/**
 * Class to help to check user information
 *
 * @author Rodrigo Duarte Sousa - rodrigodsousa@gmail.com
 */
public class AuthBean {
    public static final String AUTH_SEPARATOR = ":";


    private String userJID;
    private String transactionID;


    private AuthBean(String userJID, String transactionID) {
        this.userJID = userJID;
        this.transactionID = transactionID;
    }


    public static AuthBean createAuthBean(Request request) {
        String userJID = getRequestUserJID(request);
        String transactionID = getRequestTransactionID(request);
        return userJID != null && transactionID != null ? new AuthBean(userJID, transactionID) : null;
    }

    public static AuthBean createAuthBean(String authToken) {
        String userJID = null;
        String transactionID = null;
        String[] split = decodeAuth(authToken).split(AUTH_SEPARATOR);
        if (split.length == 2) {
            userJID = split[0];
            transactionID = split[1];
        }

        return userJID != null && transactionID != null ? new AuthBean(userJID, transactionID) : null;
    }

    private static String decodeAuth(String authToken) {
        Base64 decoder = new Base64(true);
        return new String(decoder.decode(authToken.getBytes()));
    }

    private static String getRequestUserJID(Request request) {
        ChallengeResponse challenge = request.getChallengeResponse();
        return challenge != null ? challenge.getIdentifier() : null;
    }

    private static String getRequestTransactionID(Request request) {
        ChallengeResponse challenge = request.getChallengeResponse();
        return challenge != null ? new String(challenge.getSecret()) : null;
    }

    public String getUserJID() {
        return userJID;
    }

    public String getTransactionID() {
        return transactionID;
    }
}
