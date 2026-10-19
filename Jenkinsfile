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

        // 1. Build Maven (كيخرج الـ .jar و target/classes)
        stage('Build Java Backend') {
            steps {
                sh 'chmod +x mvnw || true'
                sh 'rm -rf target/*.jar'
                // إذا كان عندك mvnw خدم بيه، ولا خدم بـ mvn العادي:
                sh 'mvn clean package -DskipTests'
            }
        }

        // 2. SonarQube Analysis
        stage('SonarQube Analysis') {
            steps {
                script {
                    def scannerHome = tool 'SonarQubeScanner'

                    withSonarQubeEnv('SonarQube') {
                        sh """
                            ${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=my-app-swapily \
                            -Dsonar.sources=src \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.host.url=http://sonarqube:9000 \
                            -Dsonar.token=\$SONAR_TOKEN
                        """
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        timeout(time: 2, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: false
                        }
                    }
                }
            }
        }

        // 3. Build Docker Image
        stage('Build Docker Images') {
            steps {
                script {
                    sh 'docker build -t opssaad/devsecops:latest .'
                }
            }
        }

        // 4. Push to Docker Hub
        stage('Push to Docker Hub') {
            steps {
                script {
                    sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                    sh 'docker push opssaad/devsecops:latest'
                }
            }
        }
    }

    post {
        always {
            sh 'docker logout'
        }
    }
}