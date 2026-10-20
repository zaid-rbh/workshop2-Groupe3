pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn -B sonar:sonar -Dsonar.projectKey=workshop2'
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline terminé'
        }
    }
}