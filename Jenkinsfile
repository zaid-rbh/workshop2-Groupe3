pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('docker-hub-credentials') // ID الخاص بحساب DockerHub في Jenkins
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
                // جلب المسار الصحيح للأداة المعرفة في Jenkins Tools
                def scannerHome = tool 'SonarQubeScanner'

                withSonarQubeEnv('SonarQube') {
                    sh """
                        ${scannerHome}/bin/sonar-scanner \
                        -Dsonar.projectKey=devsecops\
                        -Dsonar.sources=./main/java
                        -Dsonar.java.binaries=. \
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

        stage('Build Java Backend') {
            steps {
                dir('main/java') {
                    sh 'chmod +x mvnw'
                    // مسح أي ملف jar قديم داخل target لتفادي خلط النسخ القديمة
                    sh 'rm -rf target/*.jar'
                    // إعادة بناء التطبيق وإنشاء ملف jar جديد
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    // بناء صورة Backend و Frontend
                    sh 'docker build -t opssaad/devsecops:latest ./main/java'
                }
            }
        }

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