pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('docker-hub-credentials')
        SONAR_TOKEN = credentials('sonarqube-token')
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

    stage('SonarQube Analysis') {docker-hub-credentials
        steps {
            script {
                def scannerHome = tool 'SonarQubeScanner'

                withSonarQubeEnv('SonarQube') {
                    sh """
                        ${scannerHome}/bin/sonar-scanner \
                        -Dsonar.projectKey=my-app-swapily \
                        -Dsonar.sources=./src/main
                        -Dsonar.java.binaries=. \
                        -Dsonar.host.url=http://sonarqube:9000 \
                        -Dsonar.token=${SONAR_TOKEN}
                    """
                }
            }
        }
    }
}