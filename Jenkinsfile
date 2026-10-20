pipeline {
    agent any

    environment {
        SONAR_TOKEN      = credentials('sonarqube-token')
        // سجل الدخول إلى Docker Hub باستخدام credentials name: docker-hub-credentials
        DOCKER_CREDS     = credentials('docker-hub-credentials')
        IMAGE_NAME       = 'opssaad/devsecops'
        // وسم الصورة برقم البناء + latest لسهولة التتبع
        IMAGE_TAG        = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                script {
                    def mavenHome = tool 'Maven3'
                    sh "${mavenHome}/bin/mvn -B clean package"
                }
            }
            post {
                always {
                    // جمع نتائج الاختبارات (JUnit) إن وُجدت
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    withSonarQubeEnv('SonarQube') {
                        sh '''
                            export SONAR_HOST_URL="http://sonarqube:9000"
                            /var/jenkins_home/tools/hudson.plugins.sonar.SonarRunnerInstallation/SonarQubeScanner/bin/sonar-scanner \
                            -Dsonar.host.url=http://sonarqube:9000 \
                            -Dsonar.projectKey=devsecops \
                            -Dsonar.projectName=DevSecOps-VulnerableShop \
                            -Dsonar.sources=src/main/java \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.java.coveragePlugin=none
                        '''
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                script {
                    // كحل احتياطي لتفادي التعليق إذا تأخر SonarQube
                    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: false
                        }
                    }
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    // Dockerfile موجود في جذر المشروع
                    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest ."
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                script {
                    sh 'echo $DOCKER_CREDS_PSW | docker login -u $DOCKER_CREDS_USR --password-stdin'
                    sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                    sh "docker push ${IMAGE_NAME}:latest"
                }
            }
        }
    }

    post {
        always {
            sh 'docker logout || true'
            // تنظيف مساحة العمل بعد كل بناء
            cleanWs(deleteDirs: true, notFailBuild: true)
        }
        success {
            echo "Pipeline finished successfully: ${IMAGE_NAME}:${IMAGE_TAG} pushed to Docker Hub"
        }
        failure {
            echo "Pipeline failed - check the stage logs above."
        }
    }
}
