pipeline {

    agent any

    tools {

        maven 'mvn'

    }

    stages {

        stage('Build') {

            steps {

                step {

                    sh 'mvn clean verify'

                }

            }

        }

    }

}