pipeline {
    agent any

    stages {
        stage('Clean Workspace') {
            steps {
                // Полная очистка рабочей директории
                cleanWs()

                // Дополнительная очистка старых логов (если остались вне workspace)
                sh '''
                    rm -f *.log || true
                    rm -f app1/target/*.log || true
                    rm -f app2/target/*.log || true
                    rm -f app3/target/*.log || true
                '''
            }
        }

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
                    // Гарантированная остановка предыдущих экземпляров
                    sh '''
                        pkill -f "app1-1.0-SNAPSHOT.jar" || true
                        pkill -f "app2-1.0-SNAPSHOT.jar" || true
                        pkill -f "app3-1.0-SNAPSHOT.jar" || true
                        sleep 2  # Пауза для завершения процессов
                    '''

                    // Запуск с новыми логами (удаляем старые перед записью)
                    sh '''
                        rm -f app1.log app2.log app3.log || true
                        nohup java -jar app1/target/app1-1.0-SNAPSHOT.jar > app1.log 2>&1 &
                        nohup java -jar app2/target/app2-1.0-SNAPSHOT.jar > app2.log 2>&1 &
                        nohup java -jar app3/target/app3-1.0-SNAPSHOT.jar > app3.log 2>&1 &
                    '''

                    // Проверка здоровья с таймаутами
                    ['8080': 'app1', '8081': 'app2', '8082': 'app3'].each { port, app ->
                        timeout(time: 120, unit: 'SECONDS') {
                            waitUntil {
                                def status = sh(
                                    script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:${port}/health || echo '${app} not ready'",
                                    returnStdout: true
                                ).trim()
                                echo "${app} health check: ${status}"
                                return status == "200"
                            }
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
            // Архивируем только свежие логи
            archiveArtifacts artifacts: '*.log', allowEmptyArchive: true
            junit 'app3/target/surefire-reports/*.xml'

            // Дополнительная очистка после выполнения
            sh '''
                rm -f nohup.out || true
            '''
        }
    }
}