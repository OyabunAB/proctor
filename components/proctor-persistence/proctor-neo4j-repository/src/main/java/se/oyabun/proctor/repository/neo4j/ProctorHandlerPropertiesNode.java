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
package se.oyabun.proctor.repository.neo4j;

import org.neo4j.graphdb.*;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ProctorHandlerPropertiesNode {

    /**
     * Materialize a proctor handler properties node
     *
     * @param configurationID to materialize
     * @param graphDatabaseService for lookup
     * @return proctor handler configurations
     * @throws ClassNotFoundException on missing class
     * @throws NoSuchMethodException on missing constructor
     * @throws InvocationTargetException when invocation fails
     * @throws IllegalAccessException when not allowed to access
     * @throws InstantiationException when instantiation fails
     */
    public static List<ProctorHandlerConfiguration> materializeProctorProperties(final Optional<String> configurationID,
                                                                                 final GraphDatabaseService graphDatabaseService)
            throws ClassNotFoundException,
                   NoSuchMethodException,
                   InvocationTargetException,
                   IllegalAccessException,
                   InstantiationException {

        List<ProctorHandlerConfiguration> resultingProctorHandlerProperties = new ArrayList<>();

        Transaction transaction = graphDatabaseService.beginTx();

        try {

            if(configurationID.isPresent()) {
                Optional<Node> propertyNode =
                        Optional.ofNullable(
                                graphDatabaseService.findNode(ProctorHandlerPropertiesNode.Labels.PROCTOR_HANDLER_PROPERTY,
                                                              "configurationID",
                                                              configurationID.get()));
                if(propertyNode.isPresent()) {

                    resultingProctorHandlerProperties.add(mapNode(propertyNode.get()));

                }

            } else {

                ResourceIterator<Node> propertyNodes =
                        graphDatabaseService.findNodes(ProctorHandlerPropertiesNode.Labels.PROCTOR_HANDLER_PROPERTY);

                while(propertyNodes.hasNext()) {

                    resultingProctorHandlerProperties.add(mapNode(propertyNodes.next()));

                }

            }

        } finally {

            transaction.success();

        }

        return resultingProctorHandlerProperties;

    }

    /**
     * Instantiate new proctor handler properties based on node
     *
     * @param propertyNode to base instance on
     * @return instance of proctor handler property
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static ProctorHandlerConfiguration mapNode(final Node propertyNode)
            throws ClassNotFoundException,
                   NoSuchMethodException,
                   InvocationTargetException,
                   IllegalAccessException,
                   InstantiationException {

        ProctorHandlerConfiguration propertiesInstance =
                (ProctorHandlerConfiguration) Class.forName(propertyNode.getProperty("class").toString())
                                                   .newInstance();
        propertiesInstance.getClass().getMethod("setConfigurationID")
                          .invoke(propertyNode.getProperty("configurationID"));
        propertiesInstance.getClass().getMethod("setHandlerType")
                          .invoke(propertyNode.getProperty("handlerType"));
        propertiesInstance.getClass().getMethod("setPattern")
                          .invoke(propertyNode.getProperty("pattern"));
        propertiesInstance.getClass().getMethod("priority")
                          .invoke(propertyNode.getProperty("priority"));

        Relationship relationship = propertyNode.getSingleRelationship(
                ProctorHandlerPropertiesNode.RelationShipTypes.PROCTOR_HANDLER_PROPERTIES_MAP,
                Direction.BOTH);

        Node specificHandlerPropertiesNode = relationship.getEndNode();

        Map<String, String> rematerializedPropertyMap = new HashMap<>();
        specificHandlerPropertiesNode
                .getAllProperties()
                .forEach((s, o) -> rematerializedPropertyMap.put(s, o.toString()));
        propertiesInstance.getClass().getMethod("setProperties").invoke(rematerializedPropertyMap);

        return propertiesInstance;

    }

    public enum Labels implements org.neo4j.graphdb.Label {

        PROCTOR_HANDLER_PROPERTY,
        PROCTOR_HANDLER_PROPERTIES_MAP

    }

    public enum RelationShipTypes implements RelationshipType {

        PROCTOR_HANDLER_PROPERTIES_MAP

    }

}
