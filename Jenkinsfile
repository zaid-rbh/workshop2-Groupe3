pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('docker-hub-credentials') // ID الخاص بحساب DockerHub في Jenkins
        SONAR_TOKEN = credentials('sonarqube-token')
        DOCKER_IMAGE = 'opssaad/devsecops-app:latest'
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

    stage('SonarQube Analysis') {
        steps {
            script {
                // جلب المسار الصحيح للأداة المعرفة في Jenkins Tools
                def scannerHome = tool 'SonarQubeScanner'

                withSonarQubeEnv('SonarQube') {
                    sh """
                        ${scannerHome}/bin/sonar-scanner \
                        -Dsonar.projectKey=my-app-devsecops \
                        -Dsonar.sources=src \
                        -Dsonar.java.binaries=target/classes \
                        -Dsonar.host.url=http://sonarqube:9000 \
                        -Dsonar.token=${SONAR_TOKEN}
                    """
                }
            }
        }
    }

        stage('Quality Gate') {
            steps {
                script {
                    // كحل احتياطي لتفادي التعليق إذا تأخر SonarQube
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        timeout(time: 2, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: false
                        }
                    }
                }
            }
        }

        stage('Build Java Application') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE} ."
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                script {
                    sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                    sh "docker push ${DOCKER_IMAGE}"
                }
            }
        }
    }
    post {
        always {
            sh 'docker logout || true'
        }
    }
}