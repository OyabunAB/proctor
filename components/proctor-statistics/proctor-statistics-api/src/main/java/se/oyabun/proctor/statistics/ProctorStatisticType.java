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
package se.oyabun.proctor.statistics;

/**
 * Proctor Statistic Types
 */
public enum ProctorStatisticType {

    PROXY_REQUEST_RECEIVED,
    PROXY_REPLY_SENT,
    PROXY_HANDLER_MATCH,
    PROXY_HANDLER_MISS;

    public static boolean matchesAny(String value) {

        for (ProctorStatisticType proctorStatisticType : ProctorStatisticType.values()) {

            if (proctorStatisticType.name()
                                    .equals(value)) {

                return true;

            }

        }

        return false;

    }

}
