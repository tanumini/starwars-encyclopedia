pipeline {
    agent any
    environment {
        DOCKER_IMAGE_BACKEND = 'starwars-backend'
        DOCKER_IMAGE_FRONTEND = 'starwars-frontend'
    }
    stages {
    stage('Checkout Backend') {
                steps {
                    git url: 'https://github.com/tanumini/starwars-encyclopedia.git', branch: 'main'
                }
            }

            // Stage to checkout the Frontend repository
            stage('Checkout Frontend') {
                steps {
                    git url: 'https://github.com/tanumini/starwars-encyclopedia-UI.git', branch: 'Master'
                }
            }
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }
        stage('Build Backend') {
            steps {
                script {
                    sh 'mvn clean install'  // For the Spring Boot backend
                }
            }
        }
        stage('Build Frontend') {
            steps {
                script {
                    sh 'cd frontend && npm install && npm run build'  // For the React frontend
                }
            }
        }
        stage('Build Docker Images') {
            steps {
                script {
                    sh 'docker build -t $DOCKER_IMAGE_BACKEND .'
                    sh 'docker build -t $DOCKER_IMAGE_FRONTEND ./frontend'
                }
            }
        }
        stage('Push Docker Images') {
            steps {
                script {
                    sh 'docker push $DOCKER_IMAGE_BACKEND'
                    sh 'docker push $DOCKER_IMAGE_FRONTEND'
                }
            }
        }
    }
}
