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
package se.oyabun.proctor.repository.filestore;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import se.oyabun.proctor.handler.properties.ProctorHandlerConfiguration;

import java.io.*;

@Component
@CacheConfig(cacheNames = { ProctorHandlerConfiguration.CACHE_NAME})
public class ProctorFilestore {

    private static final Logger log = LoggerFactory.getLogger(ProctorFilestore.class);

    /**
     * Writes property to file
     *
     * @param configurationID used as filename
     * @param properties to write to file
     * @param propertiesDirectory where file will be written
     */
    @CachePut(value = ProctorHandlerConfiguration.CACHE_NAME,
              key = ProctorHandlerConfiguration.CACHE_KEY)
    void createFile(final String configurationID,
                    final ProctorHandlerConfiguration properties,
                    final File propertiesDirectory) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        ObjectOutput out;

        try {

            out = new ObjectOutputStream(bos);
            out.writeObject(properties);
            out.flush();
            byte[] propertiesBytes = bos.toByteArray();

            FileUtils.writeByteArrayToFile(new File(propertiesDirectory,
                                                    configurationID),
                                           propertiesBytes,
                                           false);

        } catch (IOException e) {

            if(log.isErrorEnabled()) {

                log.error("Failed to write object to store.", e);

            }

        } finally {

            try {

                bos.close();

            } catch (IOException e) {

                if(log.isErrorEnabled()) {

                    log.error("Failed to close stream.", e);

                }

            }

        }

    }

    /**
     * Read property from file.
     *
     * @param configurationID to be read.
     * @param propertiesDirectory where the file exists.
     * @return materialized file.
     */
    @Cacheable(value = ProctorHandlerConfiguration.CACHE_NAME,
               key = ProctorHandlerConfiguration.CACHE_KEY)
    ProctorHandlerConfiguration readFile(final String configurationID,
                                         final File propertiesDirectory) {

        ObjectInput in = null;

        try {

            File propertyFile = FileUtils.getFile(propertiesDirectory,
                                                  configurationID);


            if (propertyFile.exists()) {

                ByteArrayInputStream bis =
                        new ByteArrayInputStream(
                                FileUtils.readFileToByteArray(propertyFile));

                in = new ObjectInputStream(bis);

                return (ProctorHandlerConfiguration) in.readObject();

            }

        } catch (Exception e){

            if(log.isErrorEnabled()) {

                log.error("Failed to materialize properties.", e);

            }

        } finally {

            try {

                if (in != null) {

                    in.close();

                }

            } catch (IOException e) {

                if(log.isErrorEnabled()) {

                    log.error("Failed to close stream.", e);

                }

            }

        }

        return null;

    }

    /**
     * Delete file.
     *
     * @param configurationID to delete
     * @param propertiesDirectory where the file exists
     */
    @CacheEvict(value = ProctorHandlerConfiguration.CACHE_NAME,
                key = ProctorHandlerConfiguration.CACHE_KEY)
    void deleteFile(final String configurationID,
                    final File propertiesDirectory) {

        File propertiesFile = FileUtils.getFile(propertiesDirectory, configurationID);

        if(propertiesFile.exists()) {

            propertiesFile.delete();

        }

    }

}
