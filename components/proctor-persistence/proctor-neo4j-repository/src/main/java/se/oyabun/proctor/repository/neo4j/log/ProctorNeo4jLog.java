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
package se.oyabun.proctor.repository.neo4j.log;

import org.neo4j.function.Consumer;
import org.neo4j.logging.Log;
import org.neo4j.logging.Logger;

public class ProctorNeo4jLog
        implements Log, Logger{

    private final org.slf4j.Logger log;

    public ProctorNeo4jLog(org.slf4j.Logger log) {

        this.log = log;

    }

    @Override
    public boolean isDebugEnabled() {

        return log.isDebugEnabled();
    }

    @Override
    public Logger debugLogger() {

        return this;
    }

    @Override
    public void debug(String s) {

        log.debug(s);

    }

    @Override
    public void debug(String s,
                      Throwable throwable) {

        log.debug(s, throwable);

    }

    @Override
    public void debug(String s,
                      Object... objects) {

        log.debug(s, objects);

    }

    @Override
    public Logger infoLogger() {

        return this;

    }

    @Override
    public void info(String s) {

        log.info(s);

    }

    @Override
    public void info(String s,
                     Throwable throwable) {

        log.info(s, throwable);

    }

    @Override
    public void info(String s,
                     Object... objects) {

        log.info(s, objects);

    }

    @Override
    public Logger warnLogger() {

        return this;

    }

    @Override
    public void warn(String s) {

        log.warn(s);

    }

    @Override
    public void warn(String s,
                     Throwable throwable) {

        log.warn(s, throwable);

    }

    @Override
    public void warn(String s,
                     Object... objects) {

        log.warn(s, objects);

    }

    @Override
    public Logger errorLogger() {

        return this;

    }

    @Override
    public void error(String s) {

        log.error(s);

    }

    @Override
    public void error(String s,
                      Throwable throwable) {

        log.error(s, throwable);

    }

    @Override
    public void error(String s,
                      Object... objects) {

        log.error(s, objects);

    }

    @Override
    public void log(String s) {

        log.info(s);

    }

    @Override
    public void log(String s,
                    Throwable throwable) {

        log.info(s, throwable);

    }

    @Override
    public void log(String s,
                    Object... objects) {

        log.info(s, objects);

    }

    @Override
    public void bulk(Consumer consumer) {

        consumer.accept(this);

    }

}
