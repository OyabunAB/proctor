#=================================================================================================
# 88888888ba   88888888ba     ,ad8888ba,      ,ad8888ba,  888888888888  ,ad8888ba,    88888888ba
# 88      "8b  88      "8b   d8"'    `"8b    d8"'    `"8b      88      d8"'    `"8b   88      "8b
# 88      ,8P  88      ,8P  d8'        `8b  d8'                88     d8'        `8b  88      ,8P
# 88aaaaaa8P'  88aaaaaa8P'  88          88  88                 88     88          88  88aaaaaa8P'
# 88""""""'    88""""88'    88          88  88                 88     88          88  88""""88'
# 88           88    `8b    Y8,        ,8P  Y8,                88     Y8,        ,8P  88    `8b
# 88           88     `8b    Y8a.    .a8P    Y8a.    .a8P      88      Y8a.    .a8P   88     `8b
# 88           88      `8b    `"Y8888Y"'      `"Y8888Y"'       88       `"Y8888Y"'    88      `8b
#=================================================================================================
# Proctor Docker image configuration
#=================================================================================================
FROM openjdk:8-jdk
LABEL maintainer "daniel.sundberg@oyabun.se"

RUN apt-get update && apt-get install -y git curl && rm -rf /var/lib/apt/lists/*

ENV PROCTOR_HOME /usr/local/proctor

ARG user=proctor
ARG group=proctor
ARG uid=1000
ARG gid=1000

##
# Proctor is run with user `proctor`, uid = 1000
##
RUN groupadd -g ${gid} ${group} && \
    useradd -d "$PROCTOR_HOME" -u ${uid} -g ${gid} -m -s /bin/bash ${user}

##
# Make proctor home directory a volume, so configuration and build history
# can be persisted and survive image upgrades
##
VOLUME /usr/local/proctor

##
# Expose HTTP/S ports on container
##
EXPOSE 80
EXPOSE 443

USER ${user}

##
# Set up application/properties to configure
##
ARG properties=applications/proctor-application/defaults/proctor_default.properties
ARG application=applications/proctor-application/target/proctor-application.jar
ARG version=

COPY $application $PROCTOR_HOME/proctor-application.jar
COPY $properties $PROCTOR_HOME/proctor.properties

ENTRYPOINT ["/usr/local/proctor/proctor-application.jar"]