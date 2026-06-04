pipeline {
    agent { label 'jim-jam' }

    stages {

        stage('Debug Java') {
    steps {
        sh '''
            whoami
            java -version
            javac -version
            echo $JAVA_HOME
            pwd
        '''
    }
}

        stage('Deploy') {
            steps {
                sh 'docker compose down || true'
                sh 'docker compose up --build -d'
            }
        }
    }
}