pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo "Java VERSION"
                sh 'java -version'
                
                echo "Maven VERSION"
                sh 'mvn -version'
                
                echo 'building project...'
                sh "mvn compile"
                sh "mvn package"
                
                //sh "mvn test"
                sh "mvn clean install"
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}