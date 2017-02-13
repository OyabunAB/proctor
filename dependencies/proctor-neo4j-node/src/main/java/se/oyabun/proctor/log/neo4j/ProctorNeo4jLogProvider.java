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
package se.oyabun.proctor.log.neo4j;

import org.neo4j.logging.Log;
import org.neo4j.logging.LogProvider;
import org.slf4j.LoggerFactory;

public class ProctorNeo4jLogProvider
        implements LogProvider {

    @Override
    public Log getLog(Class aClass) {

        return new ProctorNeo4jLog(LoggerFactory.getLogger(aClass));
    }

    @Override
    public Log getLog(String s) {

        return new ProctorNeo4jLog(LoggerFactory.getLogger(s));

    }
}
