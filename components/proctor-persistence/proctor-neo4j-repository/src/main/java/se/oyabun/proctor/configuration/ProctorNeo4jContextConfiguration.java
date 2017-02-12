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
package se.oyabun.proctor.configuration;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.oyabun.proctor.repository.neo4j.log.ProctorNeo4jLogProvider;

import java.io.File;
import java.io.IOException;

@Configuration
public class ProctorNeo4jContextConfiguration {


    @Bean
    public GraphDatabaseService graphDatabaseService(@Value("${se.oyabun.proctor.repository.neo4j.directory:./data/neo4j}")
                                                     final String dataDirectoryProperty)
            throws IOException {

        return new GraphDatabaseFactory()
                .setUserLogProvider(new ProctorNeo4jLogProvider())
                .newEmbeddedDatabase(new File(dataDirectoryProperty));

    }

}
