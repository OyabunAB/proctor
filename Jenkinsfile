#!/usr/bin/env groovy
/*
 * =================================================================================================
 * 88888888ba   88888888ba     ,ad8888ba,      ,ad8888ba,  888888888888  ,ad8888ba,    88888888ba
 * 88      "8b  88      "8b   d8"'    `"8b    d8"'    `"8b      88      d8"'    `"8b   88      "8b
 * 88      ,8P  88      ,8P  d8'        `8b  d8'                88     d8'        `8b  88      ,8P
 * 88aaaaaa8P'  88aaaaaa8P'  88          88  88                 88     88          88  88aaaaaa8P'
 * 88""""""'    88""""88'    88          88  88                 88     88          88  88""""88'
 * 88           88    `8b    Y8,        ,8P  Y8,                88     Y8,        ,8P  88    `8b
 * 88           88     `8b    Y8a.    .a8P    Y8a.    .a8P      88      Y8a.    .a8P   88     `8b
 * 88           88      `8b    `"Y8888Y"'      `"Y8888Y"'       88       `"Y8888Y"'    88      `8b
 *=================================================================================================
 * Proctor Jenkins CI/CD configuration
 *=================================================================================================
 */
pipeline {

    agent any

    /*
     * Define build stages
     */
    stages {

        stage("Prepare environment") {

            steps {

                milestone(ordinal: '1', label: 'Prepare pipeline environment')

                def pom = readMavenPom file: 'pom.xml'

                echo 'Pom version: ${pom.version}'
            }

        }

        /*
         * Build project with maven
         */
        stage("Build Proctor"){

            steps {

                milestone(ordinal: '2', label: 'Build and prepare image')

                echo 'Pom version: ${pom.version}'

                sh 'mvn verify'

                sh 'docker build -f ./Dockerfile \
                                 -t oyabunab/proctor:0.0.1-SNAPSHOT \
                                 --build-arg version=0.0.1-SNAPSHOT'

            }

        }

        /*
         * Test
         */
        stage("Test Proctor ${POM_VERSION}") {

            steps {

                echo 'Pom version: ${pom.version}'

            }

        }

    }

    /*
     * Set handling of pipeline conditions
     */
    post {

        failure {



        }

    }

}