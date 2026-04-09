pipeline {
    agent { label 'docker-agent' }

    options {
        skipDefaultCheckout(true)
        timestamps()
    }

    environment {
        REGISTRY = 'localhost:5000'
        IMAGE_NAME = 'address-book'
        IMAGE_TAG = "${REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER}"
        NETWORK = 'jenkins-net'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './gradlew build -x test'
            }
        }

        stage('Unit Test') {
            steps {
                sh './gradlew test'
            }
            post {
                always {
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        stage('Code Quality') {
            steps {
                sh './gradlew checkstyleMain spotbugsMain'
            }
            post {
                always {
                    recordIssues(
                        tools: [
                            checkStyle(pattern: 'build/reports/checkstyle/*.xml'),
                            spotBugs(pattern: 'build/reports/spotbugs/*.xml', useRankAsPriority: true)
                        ]
                    )
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${IMAGE_TAG} ."
            }
        }

        stage('Scan Image') {
            steps {
                sh "trivy image --format table -o trivy-report.txt ${IMAGE_TAG}"
                sh "trivy image --severity CRITICAL --exit-code 1 ${IMAGE_TAG}"
            }
            post {
                always {
                    archiveArtifacts artifacts: 'trivy-report.txt', allowEmptyArchive: true
                }
            }
        }

        stage('Push to Registry') {
            steps {
                sh "docker push ${IMAGE_TAG}"
            }
        }

        stage('Deploy') {
            steps {
                sh """
                    docker stop ${IMAGE_NAME} || true
                    docker rm ${IMAGE_NAME} || true
                    docker run -d \\
                        --name ${IMAGE_NAME} \\
                        --network ${NETWORK} \\
                        -p 8081:8080 \\
                        ${IMAGE_TAG}
                """
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    echo "Waiting for application to start..."
                    for i in $(seq 1 30); do
                        if curl -sf http://address-book:8080/health > /dev/null 2>&1; then
                            echo "Application is healthy!"
                            exit 0
                        fi
                        sleep 2
                    done
                    echo "Health check failed after 60 seconds"
                    exit 1
                '''
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully! Address book is deployed at http://localhost:8081'
        }
        failure {
            echo 'Pipeline failed. Check the console output for details.'
        }
    }
}
