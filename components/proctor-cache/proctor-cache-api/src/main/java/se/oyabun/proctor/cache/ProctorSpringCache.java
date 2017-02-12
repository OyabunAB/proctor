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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

import java.util.Objects;

public class ProctorSpringCache
        implements ProctorCache {

    private static final Logger log = LoggerFactory.getLogger(ProctorSpringCache.class);

    private Cache springCache;

    public ProctorSpringCache(Cache springCache) {

        if(log.isDebugEnabled()) {

            log.debug("Initializing cache '{}'.", springCache.getName());

        }

        this.springCache = springCache;

    }


    @Override
    public String getName() {

        return springCache.getName();

    }

    @Override
    public Object getNativeCache() {

        return springCache.getNativeCache();

    }

    @Override
    public ValueWrapper get(Object key) {

        ValueWrapper valueWrapper = springCache.get(key);

        if(log.isDebugEnabled()) {

            if(Objects.nonNull(valueWrapper.get())) {

                log.debug("Hit cache for key '{}' in '{}'.", key, springCache.getName());

            } else {

                log.debug("Missed cache for key '{}' in '{}'.", key, springCache.getName());

            }

        }

        return springCache.get(key);

    }

    @Override
    public <T> T get(Object key,
                     Class<T> type) {

        T value = springCache.get(key, type);

        if(log.isDebugEnabled()) {

            if(Objects.nonNull(value)) {

                log.debug("Hit cache for key '{}' in '{}'.", key, springCache.getName());

            } else {

                log.debug("Missed cache for key '{}' in '{}'.", key, springCache.getName());

            }

        }

        return value;

    }

    @Override
    public void put(Object key,
                    Object value) {

        if(log.isDebugEnabled()) {

            log.debug("Populating cache for key '{}' in '{}'.", key, springCache.getName());

        }

        springCache.put(key, value);

    }

    @Override
    public ValueWrapper putIfAbsent(Object key,
                                    Object value) {

        ValueWrapper valueWrapper = springCache.putIfAbsent(key, value);

        if(log.isDebugEnabled()) {

            if(Objects.nonNull(valueWrapper.get())) {

                log.debug("Populating cache for key '{}' in '{}'.", key, springCache.getName());

            }

        }

        return valueWrapper;

    }

    @Override
    public void evict(Object key) {

        if(log.isDebugEnabled()) {

            log.debug("Evicting cache for key '{}' in '{}'.", key, springCache.getName());

        }

        springCache.evict(key);

    }

    @Override
    public void clear() {

        if(log.isDebugEnabled()) {

            log.debug("Clearing cache '{}'.", springCache.getName());

        }

        springCache.clear();

    }

}
