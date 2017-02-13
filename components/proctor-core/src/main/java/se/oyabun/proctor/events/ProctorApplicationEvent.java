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
package se.oyabun.proctor.events;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class ProctorApplicationEvent<S>
        extends ApplicationEvent
        implements ProctorProxyEvent {

    private final String eventID;

    public ProctorApplicationEvent(S source) {

        super(source);

        eventID = UUID.randomUUID()
                      .toString();

    }

    @Override
    public String getEventID() {

        return eventID;

    }

    @Override
    public S getSource() {

        return (S) super.getSource();

    }

}
