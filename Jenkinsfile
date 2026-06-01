pipeline {
    agent { label 'jim-jam' }

    stages {

        stage('Deploy') {
            steps {
                sh 'docker compose down || true'
                sh 'docker compose up --build -d'
            }
        }
    }
}