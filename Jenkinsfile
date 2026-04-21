pipeline {
    agent any

    tools {
        jdk 'JDK21'
    }

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
                    string(credentialsId: 'db-name', variable: 'DB_NAME'),
                    string(credentialsId: 'db-user', variable: 'DB_USER'),
                    string(credentialsId: 'db-password', variable: 'DB_PASSWORD')
                ]) {
                    sh """
                    docker stop ${CONTAINER_NAME} || true
                    docker rm ${CONTAINER_NAME} || true
                    docker run -d \
                        --name ${CONTAINER_NAME} \
                        --network ${NETWORK_NAME} \
                        --restart unless-stopped \
                        -p 8090:8080 \
                        -e SPRING_DATASOURCE_URL=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME} \
                        -e SPRING_DATASOURCE_USERNAME=${DB_USER} \
                        -e SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD} \
                        ${IMAGE_NAME}:latest
                    """
                }
            }
        }
    }
}