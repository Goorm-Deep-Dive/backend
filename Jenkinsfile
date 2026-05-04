pipeline {
    agent any

    environment {
        IMAGE_NAME = 'spring-app'
        CONTAINER_NAME = 'spring-app'
        NETWORK_NAME = 'docker-compose_app-network'
        DB_HOST = 'postgres'
        DB_PORT = '5432'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh '''
                chmod +x ./gradlew
                ./gradlew build -x test
                '''
            }
        }
        stage('Docker Build') {
            steps {
                sh """
                docker build -t ${IMAGE_NAME}:latest .
                """
            }
        }
        stage('Deploy') {
            steps {
                withCredentials([
                    file(credentialsId: 'application.yml', variable: 'APP_YML'),
                    string(credentialsId: 'db-name', variable: 'DB_NAME'),
                    string(credentialsId: 'db-user', variable: 'DB_USER'),
                    string(credentialsId: 'db-password', variable: 'DB_PASSWORD')
                ]) {
                    sh '''
                    cp "$APP_YML" ./application.yml
                    chmod 644 ./application.yml

                    echo "current dir: $(pwd)"
                    ls -al ./application.yml

                    docker stop "$CONTAINER_NAME" || true
                    docker rm "$CONTAINER_NAME" || true

                    docker create \
                        --name "$CONTAINER_NAME" \
                        --network "$NETWORK_NAME" \
                        --restart unless-stopped \
                        -p 8090:8080 \
                        "$IMAGE_NAME:latest" \
                        --spring.config.location=file:/app/application.yml

                    docker cp ./application.yml "$CONTAINER_NAME:/app/application.yml"

                    docker start "$CONTAINER_NAME"
                    '''
                }
            }
        }
        stage('Health Check') {
            steps {
                script {
                    def maxRetry = 10
                    def success = false

                    for(int i = 0; i < maxRetry; i++) {
                        def status = sh(
                            script: "curl -s http://3.37.170.214:8090/actuator/health | grep UP || true",
                            returnStdout : true
                        ).trim()

                        if(status.contains("UP")) {
                            success = true
                            echo "Health Check 성공"
                            break
                        }

                        echo "Health Check 재시도 중... (${i+1}/${maxRetry})"
                        sleep 5
                    }

                    if(!success) {




                        /*         def errorStatus = sh(
                                                script: "curl -s -o /dev/null -w '%{http_code}' http://3.37.170.214:8090/actuator/health || true",
                                                returnStdout: true
                                            ).trim()

                                            def errorLog = sh(
                                                script: "docker logs --tail 80 ${CONTAINER_NAME} 2>&1 || echo 로그 없음",
                                                returnStdout: true
                                            ).trim()

                                            env.ERROR_STATUS = errorStatus
                                            env.ERROR_LOG    = errorLog */


                        error("Health Check 실패")
                    }

                }
            }
        }
    }

    post {
        success {
            script {
                def author = sh(
                    script: "git log -1 --pretty=format:'%an'",
                    returnStdout: true
                ).trim()

                def message = sh(
                    script: "git log -1 --pretty=format:'%s'",
                    returnStdout: true
                ).trim()

                def duration = currentBuild.durationString
                    .replace(' and counting', '')

                withCredentials([
                    string(credentialsId: 'discord-webhook', variable: 'DISCORD_WEBHOOK')
                ]) {

                    writeFile file: 'discord-success.json', text: """
                    {
                      "content": "✅ 배포 성공\\n작성자: ${author}\\n커밋: ${message}\\n실행 시간: ${duration}\\n빌드 번호: #${BUILD_NUMBER}\\nURL: ${BUILD_URL}"
                    }
                    """

                    sh '''
                    curl -H "Content-Type: application/json" \
                         -X POST \
                         -d @discord-success.json \
                         "$DISCORD_WEBHOOK"
                    '''
                }
            }
        }

        failure {
            script {
                def author = sh(
                    script: "git log -1 --pretty=format:'%an' || echo unknown",
                    returnStdout: true
                ).trim()

                def message = sh(
                    script: "git log -1 --pretty=format:'%s' || echo unknown",
                    returnStdout: true
                ).trim()

                def duration = currentBuild.durationString
                    .replace(' and counting', '')

				// 메인 채널 실패 알림
                withCredentials([
                    string(credentialsId: 'discord-webhook', variable: 'DISCORD_WEBHOOK')
                ]) {

                    writeFile file: 'discord-failure.json', text: """
                    {
                      "content": "❌ 배포 실패\\n작성자: ${author}\\n커밋: ${message}\\n실행 시간: ${duration}\\n빌드 번호: #${BUILD_NUMBER}\\nURL: ${BUILD_URL}"
                    }
                    """

                    sh '''
                    curl -H "Content-Type: application/json" \
                         -X POST \
                         -d @discord-failure.json \
                         "$DISCORD_WEBHOOK"
                    '''
                }
            }
        }
    }
}
