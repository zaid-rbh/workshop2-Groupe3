pipeline {
    agent any

    tools {
        maven 'Maven3'                  // تأكد من مطابقة الاسم في Manage Jenkins -> Tools
        sonarScanner 'SonarQubeScanner' // تأكد من مطابقة الاسم في Manage Jenkins -> Tools
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build & Compile Java') {
            steps {
                // تجميع المشروع وإنشاء target/classes قبل مرحلة السكنر
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

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-credentials-id', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push opssaad/vulnerableshop:latest
                    '''
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