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

                milestone(ordinal: '1', label: 'Perparation')

                readMavenPom(file: 'pom.xml')


                        echo 'test1'



            }

        }

        /*
         * Build project with maven
         */
        stage("Build Proctor"){

            steps {

                milestone(ordinal: '1', label: 'Perparation')


                "Build proctor ${POM_VERSION} docker image" {

                }

            }


        }

        /*
         * Test
         */
        stage("Test Proctor ${POM_VERSION}") {

            steps {

                echo '${POM_VERSION}'



            }


        }

    }

    /*
     * Set handling of pipeline conditions
     */
    post {

        failure {

            mail to: daniel.sundberg@oyabun.se, subject: 'Proctor pipeline failed'

        }

    }

}