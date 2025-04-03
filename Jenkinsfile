pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/IgorP17/Ganesh.git'
            }
        }

        stage('Build Apps') {
            steps {
                dir('app1') { sh 'mvn package' }
                dir('app2') { sh 'mvn package' }
                dir('app3') { sh 'mvn package' }
            }
        }

        stage('Start Apps') {
            steps {
                script {
                    // Запускаем приложения в фоне с логированием
                    sh '''
                        nohup java -jar app1/target/app1-0.0.1-SNAPSHOT.jar > app1.log 2>&1 &
                        nohup java -jar app2/target/app2-0.0.1-SNAPSHOT.jar > app2.log 2>&1 &
                        nohup java -jar app3/target/app3-0.0.1-SNAPSHOT.jar > app3.log 2>&1 &
                    '''

                    // Ждем готовности app3 (порт 8082)
                    timeout(time: 120, unit: 'SECONDS') {
                        waitUntil {
                            def status = sh(
                                script: 'curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/health',
                                returnStdout: true
                            ).trim()
                            return status == "200"
                        }
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                dir('app3') {
                    sh 'mvn test -Dtest=MessageFlowTest'
                }
            }
        }

        stage('Stop Apps') {
            steps {
                script {
                    sh '''
                        pkill -f "app1-0.0.1-SNAPSHOT.jar" || true
                        pkill -f "app2-0.0.1-SNAPSHOT.jar" || true
                        pkill -f "app3-0.0.1-SNAPSHOT.jar" || true
                    '''
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/*.log', allowEmptyArchive: true
            junit 'app3/target/surefire-reports/*.xml'
        }
    }
}