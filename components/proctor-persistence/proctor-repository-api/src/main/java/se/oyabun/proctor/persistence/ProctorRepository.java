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
package se.oyabun.proctor.persistence;

import se.oyabun.proctor.events.ProctorProxyEvent;
import se.oyabun.proctor.handler.properties.ProctorHandlerProperties;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Proctor repository API
 */
public interface ProctorRepository {

    boolean containsPropertyKey(final String configurationID);

    void persistProperty(final ProctorHandlerProperties properties);

    Optional<ProctorHandlerProperties> getProperty(final String configurationID);

    Stream<String> getPropertyKeys();

    Stream<ProctorHandlerProperties> getProperties();

    void deleteProperty(final String configurationID);

    boolean containsProxyEventKey(final String eventID);

    void persistEvent(final ProctorProxyEvent proxyEvent);

    Optional<ProctorProxyEvent> getProxyEvent(final String eventID);

    Stream<String> getProxyEventKeys();

    Stream<ProctorProxyEvent> getProxyEvents();

    void deleteProxyEvent(final String eventID);

}
