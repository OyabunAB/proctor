/**
 *
 * Copyright 2016 Oyabun AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
**/
(function() {

    'use strict';

    angular.module('proctoradmin')
        .controller('ClusterController', clusterController);

    function clusterController($scope,
                               $interval,
                               proctorLogger,
                               proctorCluster) {

        var model = this;

        model.nodes = [];
        model.updateNodes = updateNodes;

        model.nodeCardViewConfiguration = {
            selectItems: false,
            multiSelect: false,
            dblClick: false,
            selectionMatchProp: 'nodeID',
            selectedItems: [],
            checkDisabled: false,
            showSelectBox: false,
            onSelect: null,
            onSelectionChange: null,
            onCheckBoxChange: null,
            onClick: null,
            onDblClick: null
        };

        initController();

        /**
         * Initialize the controller, setting up intervals and preparing
         * to destroy them on scope destruction.
         */
        function initController() {

            //
            // Do first update
            //
            model.updateNodes();

            //
            // Instantiate interval updaters
            //
            var updateNodesPromise =
                $interval( function(){ model.updateNodes(); }, 5000);

            //
            // Register callbacks to cancel intervals on destroy
            //
            $scope.$on('$destroy',function() {

                if(updateNodesPromise) $interval.cancel(updateNodesPromise);

            });

        }

        /**
         * Request an update of cluster nodes
         */
        function updateNodes() {

            var clusterRequestCallback =
                function(nodes) {

                    if(nodes) {

                        model.nodes = nodes;

                    } else {

                        model.nodes = [];

                    }


                };

            proctorCluster.getNodes(clusterRequestCallback);

        }

    };

})();