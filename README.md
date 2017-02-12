
    =============================================================================
     88888888ba
     88      "8b                                     ,d
     88      ,8P                                     88
     88aaaaaa8P' 8b,dPPYba,  ,adPPYba,   ,adPPYba, MM88MMM ,adPPYba,  8b,dPPYba,
     88""""""'   88P'   "Y8 a8"     "8a a8"     ""   88   a8"     "8a 88P'   "Y8
     88          88         8b       d8 8b           88   8b       d8 88
     88          88         "8a,   ,a8" "8a,   ,aa   88,  "8a,   ,a8" 88
     88          88          `"YbbdP"'   `"Ybbd8"'   "Y888 `"YbbdP"'  88
    =============================================================================
# Proctor Open Proxy Framework [![Build Status](http://jenkins.oyabun.se/buildStatus/icon?job=Proctor development)](http://jenkins.oyabun.se/job/Proctor development)
Is slowly filling  up with that awsome-sause.


## Project structure
A high level project overview and road map.

    ╔═════════════════════╗ 
    ║ Proctor Core Module ║
    ╚═╤═══════════════════╝ 
      │   ╔═══════════════════╗
      ├───╢ Proctor Proxy API ║
      │   ╚═╤═════════════════╝
      │     │   ╔══════════════════════╗
      │     ├───╢ Proctor Netty Module ║
      │     │   ╚══════════════════════╝
      │     │   ╔════════════════════════╗
      │     └───╢ Proctor Grizzly Module ║
      │         ╚════════════════════════╝
      │   ╔════════════════════╗
      ├───╢ Proctor Client API ║
      │   ╚═╤══════════════════╝
      │     │   ╔═════════════════════════════════╗
      │     ├───╢ Proctor NING HTTP Client Module ║
      │     │   ╚═════════════════════════════════╝
      │     │   ╔═══════════════════════════════════╗
      │     └───╢ Proctor Apache HTTP Client Module ║
      │         ╚═══════════════════════════════════╝
      │   ╔═════════════════════╗
      ├───╢ Proctor Handler API ║
      │   ╚═╤═══════════════════╝
      │     │   ╔═════════════════════════════════════╗
      │     ├───╢ Proctor Static Route Handler Module ║
      │     │   ╚═════════════════════════════════════╝
      │     │   ╔══════════════════════════════════╗
      │     └───╢ Proctor ZooKeeper Handler Module ║
      │         ╚══════════════════════════════════╝
      │   ╔═══════════════════╗
      ├───╢ Proctor Cache API ║
      │   ╚═╤═════════════════╝
      │     │   ╔════════════════════════════════╗
      │     └───╢ Proctor Hazelcast Cache Module ║
      │         ╚════════════════════════════════╝
      │   ╔════════════════════════╗
      ├───╢ Proctor Repository API ║
      │   ╚═╤══════════════════════╝
      │     │   ╔═════════════════════════════════════╗
      │     ├───╢ Proctor Filestore Repository Module ║
      │     │   ╚═════════════════════════════════════╝
      │     │   ╔═════════════════════════════════╗
      │     └───╢ Proctor Neo4J Repository Module ║
      │         ╚═════════════════════════════════╝
      │   ╔════════════════════════╗
      ├───╢ Proctor Statistics API ║
      │   ╚═╤══════════════════════╝
      │     │   ╔═══════════════════════════════════╗
      │     └───╢ Proctor DropWizard Metrics Module ║
      │         ╚═══════════════════════════════════╝
      │   ╔════════════════════════════╗
      └───╢ Proctor Administration GUI ║
          ╚═╤══════════════════════════╝
            │   ╔══════════════════════════════════╗
            ├───╢ Proctor PatternFly WebJar Module ║
            │   ╚══════════════════════════════════╝
            │   ╔══════════════════════════════════════╗
            ├───╢ Proctor Administration WebJar Module ║
            │   ╚══════════════════════════════════════╝
            │   ╔════════════════════════════════════════════╗
            └───╢ Proctor Administration REST Service Module ║
                ╚════════════════════════════════════════════╝
    ╔═════════════════════╗
    ║ Proctor Util Module ║
    ╚═════════════════════╝

## Proctor Core Module
The proctor core module contains all shared resources and exceptions 
in order to minimize sharding of common components. It will also
have dependencies for logging and other general dependencies.

API modules will have a dependency on this module.
 
## Proctor Proxy API 
The proxy API conforms all interactions with the actual incoming and
outgoin message handling. A simplified interface and abstract proxy
implementation is provided.

The proxy API implementation is intended to use the Proctor HTTP client
API to do proxied calls to handler provided lookups.

#### Proctor Netty Module
A Netty implementation of the Proctor Proxy API utilizing the Netty
framework to manage incoming and outgoing HTTP communication.
 
https://netty.io/
 
#### Proctor Grizzly Module
A Grizzly implementation of the Proctor Proxy API utilizing the Grizzly
framwork to manage incoming and outgoing HTTP communication.

https://grizzly.java.net/
      
## Proctor Client API
The HTTP client API provides a common interaction with a client
implementation to execute requests and receive responses. Used by
proxy implementations.

#### Proctor NING Module
A NING Asynchronous http client implementation of the Proctor 
client API. Its built on top of the Netty framework, so its a nice 
companion to the Netty Proxy Module. 

https://github.com/AsyncHttpClient/async-http-client/ 
 
#### Proctor Apache HTTPClient Module
A Apache HTTPClient Asynchronous http client implementation of the 
Proctor client API. Not my cup of tea, but what the hey, its fun to 
verify that we can implement modules for different implementations.

https://hc.apache.org/httpcomponents-client-ga/

## Proctor Handler API
The proctor handlers are the heart of the Proctor Proxy and should be
used through the ProctorHandlerManager that provides a reference to all
registered proctor handlers. The handlers matches given URIs to provide
a handle name which in turn can be used to get an endpoint for proxying.

#### Proctor Static Route Handler Module
A static route handler which constructs with a given regexp and returns
a static URL. Used by the administration GUI to proxy requests to the
(optional) administration web interface.

#### Proctor ZooKeeper Handler Module
A ZooKeeper connected route handler which does searches in the registry
through Curator when queried for matches on URIs.

https://zookeeper.apache.org/
https://curator.apache.org/

## Proctor Cache API
The cache API provides the handler configurations with a caching layer and 
has been introduced to enhance performance and enable distribution of configuration
for scalability.

#### Proctor Hazelcast Cache Module
A cache implementation using a Hazelcast embedded node, enabling distributed caching 
and other features from the framework.

https://hazelcast.com/

## Proctor Repository API
The repository API is introduced to enable persistent configuration between 
application restarts. Indirectly dependant on caching components though the
_Spring Cache API_.

#### Proctor Filestore Repository Module
The file store module is a simple serialization/deserialization of the proctor 
handler configuration objects to a file structure.

#### Proctor Neo4j Repository Module
The Neo4j repository module enables the framework to have a scalable distributed
persistence layer through a embedded Neo4j graph database. 

https://neo4j.com/

## Proctor Statistics API 
The statistics API provides a structured way to access and provide
metrics for the proxy. 

#### Proctor DropWizard Metrics Module
A statistics API implementation with the DropWizard Metrics framework.

https://metrics.dropwizard.io/
