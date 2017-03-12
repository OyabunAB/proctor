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
  stages {
    stage('Prepare environment') {
      steps {
        milestone(ordinal: 1, label: 'Prepare pipeline environment')
      }
    }
    stage('Build Proctor') {
      steps {
        milestone(ordinal: 2, label: 'Build and test code')
        sh 'mvn verify'
      }
    }
    stage('') {
      steps {
        milestone(ordinal: 3, label: 'Prepare docker image')
        sh 'docker build -t oyabunab/proctor:0.0.1-SNAPSHOT --build-arg version=0.0.1-SNAPSHOT'
      }
    }
    stage('Test Proctor') {
      steps {
        milestone(ordinal: 4, label: 'Test functionality')
        echo 'Here will be tests.'
      }
    }
  }
  post {
    failure {
      echo 'Horrible failure placeholder.'
    }
  }
}