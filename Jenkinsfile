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
                    sh 'mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=workshop2 -Dsonar.projectName=workshop2 -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.token=$SONAR_AUTH_TOKEN'
                }
            }
        }
    

    stage('Semgrep Scan') {
            steps {
                sh 'semgrep scan --config p/java --config p/owasp-top-ten --metrics=off --text --output semgrep-report.txt .'
                sh 'cat semgrep-report.txt'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'semgrep-report.txt', allowEmptyArchive: true
            echo 'Pipeline terminé'
        }
    }
}