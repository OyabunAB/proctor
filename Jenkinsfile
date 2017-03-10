pipeline {

    stages {

        stage ('Build') {

           withMaven(maven: 'mvn') { sh "mvn clean verify" }

        }

    }

}