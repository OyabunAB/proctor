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
package se.oyabun.proctor.web.admin.api.cluster.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import se.oyabun.proctor.ProctorServerConfiguration;
import se.oyabun.proctor.cluster.manager.ProctorClusterManager;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Proctor Cluster REST API
 *
 * @version 1
 */
@RestController
@RequestMapping(value = ClusterRestController.CLUSTER_ROOT)
public class ClusterRestController {

    public static final String CLUSTER_ROOT = "/api/v1/cluster";
    public static final String NODES = "/nodes";

    private final ProctorClusterManager proctorClusterManager;

    @Autowired
    public ClusterRestController(final ProctorClusterManager proctorClusterManager) {

        this.proctorClusterManager = proctorClusterManager;

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = ClusterRestController.NODES,
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<ProctorServerConfiguration[]> getNodes() {

        final ProctorServerConfiguration[] proctorServerConfigurations =
                proctorClusterManager.getServers()
                                     .toArray(size -> new ProctorServerConfiguration[size]);

        return !Arrays.asList(proctorServerConfigurations).isEmpty() ?
               ResponseEntity.status(HttpStatus.OK).body(proctorServerConfigurations) :
               ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ProctorServerConfiguration[0]);


    }

    public ResponseEntity<ClusterNodeStatus> getStatus() {

        return ResponseEntity.status(HttpStatus.OK).body(new ClusterNodeStatus());

    }

    public class ClusterNodeStatus implements Serializable {

        final String proxyAddressAndPort = proctorClusterManager.getThisServer().getProxyAddressAndPort();
        final Integer processorCount = Runtime.getRuntime().availableProcessors();
        final Long freeMemory = Runtime.getRuntime().freeMemory();
        final Long maximumMemory = Runtime.getRuntime().maxMemory();
        final Long totalMemory = Runtime.getRuntime().totalMemory();

        public String getProxyAddressAndPort() {

            return proxyAddressAndPort;
        }

        public Integer getProcessorCount() {

            return processorCount;
        }

        public Long getFreeMemory() {

            return freeMemory;
        }

        public Long getMaximumMemory() {

            return maximumMemory;
        }

        public Long getTotalMemory() {

            return totalMemory;
        }
    }

}
