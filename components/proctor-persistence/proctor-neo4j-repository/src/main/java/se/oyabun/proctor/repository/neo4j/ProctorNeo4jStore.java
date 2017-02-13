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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;

import java.util.List;
import java.util.Optional;

@Component
public class ProctorNeo4jStore {

    private static final Logger log = LoggerFactory.getLogger(ProctorNeo4jStore.class);

    /**
     * Cached persist of properties
     *
     * @param configurationID for cache key
     * @param properties to persist and add to cache
     */
    @CachePut(value = ProctorHandlerConfiguration.CACHE_NAME,
              key = ProctorHandlerConfiguration.CACHE_KEY)
    void createNode(final String configurationID,
                    final ProctorHandlerConfiguration properties,
                    final GraphDatabaseService graphDatabaseService) {

        if(log.isDebugEnabled()) {

            log.debug("Persisting property for ID: '{}'", configurationID);

        }

        Transaction transaction = graphDatabaseService.beginTx();
        try {

            Node handlerPropertiesNode =
                    graphDatabaseService.createNode(
                            ProctorHandlerPropertiesNode.Labels.PROCTOR_HANDLER_PROPERTY);

            handlerPropertiesNode.setProperty("configurationID", properties.getConfigurationID());
            handlerPropertiesNode.setProperty("handlerType", properties.getHandlerType());
            handlerPropertiesNode.setProperty("pattern", properties.getPattern());
            handlerPropertiesNode.setProperty("priority", properties.getPriority());
            handlerPropertiesNode.setProperty("class", properties.getClass().getName());

            Node propertiesMapNode =
                    graphDatabaseService.createNode(
                            ProctorHandlerPropertiesNode.Labels.PROCTOR_HANDLER_PROPERTIES_MAP);

            propertiesMapNode.setProperty("configurationID", properties.getConfigurationID());
            properties.getProperties().entrySet().forEach(
                    entry -> propertiesMapNode.setProperty(entry.getKey(),
                                                           entry.getValue()));

            Relationship relationship =
                    handlerPropertiesNode.createRelationshipTo(
                            propertiesMapNode,
                            ProctorHandlerPropertiesNode.RelationShipTypes.PROCTOR_HANDLER_PROPERTIES_MAP);

            relationship.setProperty("configurationID", properties.getConfigurationID());

        } finally {

            transaction.success();

        }

    }

    /**
     * Cached getting of properties
     *
     * @param configurationID to cache-get for
     * @return cached property or getting it from the database
     */
    @Cacheable(value = ProctorHandlerConfiguration.CACHE_NAME,
               key = ProctorHandlerConfiguration.CACHE_KEY)
    ProctorHandlerConfiguration findNode(final String configurationID,
                                         final GraphDatabaseService graphDatabaseService) {

        if(log.isDebugEnabled()) {

            log.debug("Getting property for ID: '{}'.", configurationID);

        }

        try {


            List<ProctorHandlerConfiguration> materializedProperties =
                    ProctorHandlerPropertiesNode.materializeProctorProperties(Optional.of(configurationID),
                                                                              graphDatabaseService);

            if(materializedProperties.isEmpty() && materializedProperties.size() == 1) {

                return materializedProperties.iterator().next();

            }

        } catch (Exception e) {

            if(log.isErrorEnabled()) {

                log.error("Failed to materialize proctor handler properties.", e);

            }

        }

        return null;

    }

    /**
     * Cached implementation of delete property
     *
     * @param configurationID
     */
    @CacheEvict(value = ProctorHandlerConfiguration.CACHE_NAME,
                key = ProctorHandlerConfiguration.CACHE_KEY)
    void deleteNode(final String configurationID,
                    final GraphDatabaseService graphDatabaseService) {

        if(log.isDebugEnabled()) {

            log.debug("Deleting property for ID: '{}'.", configurationID);

        }

        Transaction transaction = graphDatabaseService.beginTx();

        try {

            Optional<Node> propertyNode =
                    Optional.ofNullable(
                            graphDatabaseService.findNode(
                                    ProctorHandlerPropertiesNode.Labels.PROCTOR_HANDLER_PROPERTY,
                                    "configurationID",
                                    configurationID));

            if(propertyNode.isPresent()) {

                Relationship relationship =
                        propertyNode.get().getSingleRelationship(ProctorHandlerPropertiesNode.RelationShipTypes.PROCTOR_HANDLER_PROPERTIES_MAP, Direction.BOTH);

                Node specificHandlerProperties = relationship.getEndNode();

                relationship.delete();

                specificHandlerProperties.delete();

                propertyNode.get().delete();

            }

        } finally {

            transaction.success();

        }

    }

}
