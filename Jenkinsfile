#!groovy​
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
     * Define tools usage
     */
    tools {

        maven 'mvn'
        docker 'docker'

    }

    /*
     * Define build stages
     */
    stages {

        /*
         * Build project with maven
         */
        stage("Build Proctor"){

            step("Compile") {

                sh 'mvn clean verify'

                junit '*/target/surefire-reports/*.xml'

            }

        }

        /*
         * Compile test node images
         */
        stage("Create Docker Image") {

            step("Build Image") {

                def proctor = docker.build "oyabun/proctor_test:${env.BUILD_NUMBER}";

                proctor.inside {

                    sh 'ls /usr/local/proctor'

                }

                //proctor.push

            }

        }

    }

    post {

        always {

            deleteDir()

        }

        //success {}

        //unstable {}

        //failure {}

        //changed {}

    }

}