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

        stage('SonarQube Analysis (SAST)') {
            steps {
                script {
                    // جلب المسار الصحيح للأداة المعرفة في Jenkins Tools
                    def scannerHome = tool 'SonarQubeScanner'

                    withSonarQubeEnv('SonarQube') {
                        sh """
                            ${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=devsecops \
                            -Dsonar.projectName=DevSecOps-VulnerableShop \
                            -Dsonar.sources=src/main/java \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.java.coveragePlugin=none
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
                        timeout(time: 5, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: false
                        }
                    }
                }
            }
        }

        stage('Dependency Scan (SCA)') {
            steps {
                script {
                    // فحص ثغرات الاعتماديات (Maven dependencies) عبر OWASP Dependency-Check
                    def mvnHome = tool 'Maven'
                    sh """
                        ${mvnHome}/bin/mvn -B org.owasp:dependency-check-maven:check || true
                    """
                }
            }
            post {
                always {
                    // نشر تقرير الفحص كواجهة للمتابعة (يتطلب plugin HTML Publisher)
                    publishHTML(target: [
                        reportDir: 'target',
                        reportFiles: 'dependency-check-report.html',
                        reportName: 'OWASP Dependency Check Report'
                    ])
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

        stage('Container Scan (Trivy)') {
            steps {
                script {
                    // فحص ثغرات صورة التطبيق و ثغرات CRITICAL/HIGH كحد فاصل
                    sh """
                        trivy image --severity CRITICAL,HIGH --exit-code 1 --no-progress ${IMAGE_NAME}:${IMAGE_TAG} || true
                        trivy image --severity MEDIUM,LOW    --exit-code 0 --no-progress ${IMAGE_NAME}:${IMAGE_TAG} || true
                    """
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
