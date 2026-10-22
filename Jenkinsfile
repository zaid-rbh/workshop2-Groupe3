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
    
    stage('OWASP Dependency-Check') {
            steps {
                withCredentials([string(credentialsId: 'nvd-api-key', variable: 'NVD_API_KEY')]) {
                    sh '''
                        mvn -B org.owasp:dependency-check-maven:check \
                          -DnvdApiKey=$NVD_API_KEY \
                          -DdataDirectory=/var/jenkins_home/dependency-check-data \
                          -Dformats=HTML,JSON \
                          -DfailBuildOnCVSS=11 \
                          -DossindexAnalyzerEnabled=false
                    '''
                }
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