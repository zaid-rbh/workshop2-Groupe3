pipeline {
    agent any

    tools {
        maven 'Maven3'                             // تأكد من الاسم في Manage Jenkins -> Tools
        sonarRunnerInstallation 'SonarQubeScanner' // تأكد من الاسم في Manage Jenkins -> Tools
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build & Compile Java') {
            steps {
                sh 'mvn clean compile -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    withSonarQubeEnv('SonarQube') {
                        sh '''
                            sonar-scanner \
                            -Dsonar.projectKey=swapily-app \
                            -Dsonar.projectName=swapily-app \
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