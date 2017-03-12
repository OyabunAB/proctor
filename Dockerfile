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
# Proctor Docker container image
#=================================================================================================
FROM anapsix/alpine-java:8_server-jre_unlimited

##
# Set up application/properties to configure
##
ARG properties=applications/proctor-application/defaults/proctor_default.properties
ARG application=applications/proctor-application/target/proctor-application.jar
ARG version=

##
# Document metadata
##
LABEL maintainer "daniel.sundberg@oyabun.se"
LABEL version "${version}"
LABEL description="Proctor Open Proxy Framework ${version}"
##
# Define proctor environment
##
ENV PROCTOR_CONFIG /etc/proctor/proctor.properties
ENV PROCTOR_HOME /usr/local/proctor
ENV PROCTOR_DATA /usr/local/proctor/data

##
# Set proctor configuration directory as volume
##
VOLUME /etc/proctor
VOLUME /usr/local/proctor/data

##
# Expose HTTP/S ports on container
##
EXPOSE 80
EXPOSE 443

##
# Install proctor
##
COPY $application $PROCTOR_HOME/proctor-application-$version.jar
COPY $properties $PROCTOR_CONFIG

RUN /bin/bash -c 'ln -s $PROCTOR_HOME/proctor-application-$version.jar /etc/init.d/proctor'

ENTRYPOINT ["/bin/bash"]