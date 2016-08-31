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
package se.oyabun.proctor.proxy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Proctor proxy interface
 */
public interface ProctorProxy {



    /**
     * Initiates the proxy after startup
     */
    @PostConstruct
    void startProxy() throws Exception;

    /**
     * Stops the proxy before shutdown
     */
    @PreDestroy
    void stopProxy();

}
