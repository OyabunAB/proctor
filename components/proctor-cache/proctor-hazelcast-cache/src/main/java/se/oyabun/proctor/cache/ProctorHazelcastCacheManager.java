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
package se.oyabun.proctor.cache;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ProctorHazelcastCacheManager
        implements ProctorCacheManager {

    private static final Logger log = LoggerFactory.getLogger(ProctorHazelcastCacheManager.class);

    private HazelcastCacheManager hazelcastCacheManager;

    @Autowired
    public ProctorHazelcastCacheManager(final HazelcastInstance hazelcastInstance) {

        if(log.isDebugEnabled()) {

            log.debug("Initializing Proctor Hazelcast Cache Manager.");

        }

        this.hazelcastCacheManager = new HazelcastCacheManager(hazelcastInstance);

    }

    @Override
    public ProctorCache getCache(String name) {

        if(log.isDebugEnabled()) {

            log.debug("Requesting cache '{}'.", name);

        }

        return new ProctorSpringCache(hazelcastCacheManager.getCache(name));

    }

    @Override
    public Collection<String> getCacheNames() {

        if(log.isDebugEnabled()) {

            log.debug("Requesting all cache names.");

        }

        return hazelcastCacheManager.getCacheNames();

    }

}
