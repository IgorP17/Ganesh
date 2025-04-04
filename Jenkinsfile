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
                dir('app1') { sh 'mvn clean package -DskipTests' }
                dir('app2') { sh 'mvn clean package -DskipTests' }
                dir('app3') { sh 'mvn clean package -DskipTests' }
            }
        }

        stage('Start Apps') {
            steps {
                script {
                    // Останавливаем предыдущие экземпляры
                    sh '''
                        pkill -f "app1-1.0-SNAPSHOT.jar" || true
                        pkill -f "app2-1.0-SNAPSHOT.jar" || true
                        pkill -f "app3-1.0-SNAPSHOT.jar" || true
                    '''

                    // Запускаем приложения раздельно
                    sh '''
                        nohup java -jar app1/target/app1-1.0-SNAPSHOT.jar > app1.log 2>&1 &
                        nohup java -jar app2/target/app2-1.0-SNAPSHOT.jar > app2.log 2>&1 &
                        nohup java -jar app3/target/app3-1.0-SNAPSHOT.jar > app3.log 2>&1 &
                    '''

                    // Увеличиваем таймаут и добавляем логирование для app3
                    timeout(time: 120, unit: 'SECONDS') {
                        waitUntil {
                            def status = sh(
                                script: 'curl -s -o /dev/null -w "%{http_code}" http://localhost:8082/health || echo "app3 not ready"',
                                returnStdout: true
                            ).trim()
                            echo "Health check status: ${status}"
                            return status == "200"
                        }
                    }

                    // Увеличиваем таймаут и добавляем логирование для app2
                    timeout(time: 120, unit: 'SECONDS') {
                        waitUntil {
                            def status = sh(
                                script: 'curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/health || echo "app2 not ready"',
                                returnStdout: true
                            ).trim()
                            echo "Health check status: ${status}"
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
                        pkill -f "app1-1.0-SNAPSHOT.jar" || true
                        pkill -f "app2-1.0-SNAPSHOT.jar" || true
                        pkill -f "app3-1.0-SNAPSHOT.jar" || true
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