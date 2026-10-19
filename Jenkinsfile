pipeline {
    agent any

    tools {
        sonarScanner 'SonarQubeScanner'
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build & Compile Java') {
            steps {
                // Compilation du projet pour générer target/classes
                sh 'mvn clean compile -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    withSonarQubeEnv('SonarQube') {
                        sh '''
                            sonar-scanner \
                            -Dsonar.projectKey=devsecops \
                            -Dsonar.projectName=devsecops \
                            -Dsonar.sources=src/main/java \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.host.url=http://sonarqube:9000
                        '''
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    // Vérifie le résultat du Quality Gate auprès de SonarQube
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t opssaad/vulnerableshop:latest .'
            }
        }
    }
}