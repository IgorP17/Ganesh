pipeline {
    agent any

    stages {
        stage('Clean Workspace') {
            steps {
                cleanWs()
                sh '''
                    rm -f *.log || true
                    rm -f all_logs.zip || true
                    rm -rf allure-results || true
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
                dir('app3') {
                    sh '''
                        mvn clean package -DskipTests
                        mkdir -p allure-results
                    '''
                }
            }
        }

        stage('Start Apps') {
            steps {
                script {
                    sh '''
                        pkill -f "app1-1.0-SNAPSHOT.jar" || true
                        pkill -f "app2-1.0-SNAPSHOT.jar" || true
                        pkill -f "app3-1.0-SNAPSHOT.jar" || true
                        sleep 5
                    '''
                    sh '''
                        nohup java -jar app1/target/app1-1.0-SNAPSHOT.jar > app1.log 2>&1 &
                        nohup java -jar app2/target/app2-1.0-SNAPSHOT.jar > app2.log 2>&1 &
                        nohup java -jar app3/target/app3-1.0-SNAPSHOT.jar > app3.log 2>&1 &
                    '''
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
                dir('app1') {
                    sh '''
                        mvn test -Dtest=*Test -Dallure.results.directory=./allure-results || echo "Тесты завершены с кодом $?"
                        ls -la allure-results/ # Проверка наличия файлов
                    '''
                }
                dir('app3') {
                    sh '''
                        mvn test -Dtest=*MessageFlowTest -Dallure.results.directory=./allure-results || echo "Тесты завершены с кодом $?"
                        ls -la allure-results/ # Проверка наличия файлов
                    '''
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
            script {
                // Архивируем логи приложений
                def timestamp = sh(script: 'date +"%Y-%m-%d_%H-%M-%S"', returnStdout: true).trim()
                sh """
                    mkdir -p logs_${timestamp}
                    cp *.log logs_${timestamp}/ || echo "Файлы логов не найдены"
                    zip -r all_logs_${timestamp}.zip logs_${timestamp} || echo "Ошибка архивации"
                """

                // Генерируем и сохраняем Allure отчет
                allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'app3/allure-results', path: 'app1/allure-results']],  // Измененный путь
                    report: 'allure-report'
                ])
            }

            // Архивируем все артефакты
            archiveArtifacts artifacts: 'all_logs_*.zip, app3/target/surefire-reports/*.xml, allure-report/**', allowEmptyArchive: true
            junit 'app3/target/surefire-reports/*.xml'

            // Очистка
            sh '''
                rm -rf logs_* nohup.out || true
            '''
        }
    }
}