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
package se.oyabun.proctor.web.admin.api.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.oyabun.proctor.ProctorServerConfiguration;
import se.oyabun.proctor.cluster.manager.ProctorClusterManager;
import se.oyabun.proctor.web.admin.api.AbstractSecuredAPIController;

import java.util.Arrays;

/**
 * Proctor Cluster REST API
 *
 * @version 1
 */
@RestController
@RequestMapping(value = ClusterRestController1.API_ROOT)
public class ClusterRestController1
        extends AbstractSecuredAPIController {

    public static final String API_ROOT = "/v1/api/clusters/";
    public static final String NODES = "nodes/";

    private final ProctorClusterManager proctorClusterManager;

    @Autowired
    public ClusterRestController1(final ProctorClusterManager proctorClusterManager) {

        this.proctorClusterManager = proctorClusterManager;

    }

    @RequestMapping(value = ClusterRestController1.NODES,
                    method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ProctorServerConfiguration[]> getNodes() {

        final ProctorServerConfiguration[] proctorServerConfigurations =
                proctorClusterManager.getServers()
                                     .toArray(size -> new ProctorServerConfiguration[size]);

        return !Arrays.asList(proctorServerConfigurations).isEmpty() ?
               ResponseEntity.status(HttpStatus.OK).body(proctorServerConfigurations) :
               ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ProctorServerConfiguration[0]);


    }


}
