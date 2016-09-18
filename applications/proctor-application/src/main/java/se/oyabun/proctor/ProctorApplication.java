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
package se.oyabun.proctor;

import org.springframework.boot.SpringApplication;

/**
 * Proctor application bootstrap class
 */
public class ProctorApplication {

    /**
     * Main startup method
     * @param arguments from command line
     * @throws Exception on application error
     */
    public static void main(String[] arguments) throws Exception {

        SpringApplication.run(ProctorServer.class, arguments);

    }

}
