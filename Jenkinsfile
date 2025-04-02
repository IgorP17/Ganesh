pipeline {
    agent any

    environment {
        PROJECT_DIR = "${WORKSPACE}"
        APP1_PORT = "8080"
        APP2_PORT = "8081"
        APP3_PORT = "8082"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    extensions: [],
                    userRemoteConfigs: [[
                        url: 'https://github.com/IgorP17/Ganesh.git'
                    ]]
                ])
            }
        }

        stage('Check Running Apps') {
            steps {
                script {
                    def isApp1Running = sh(script: "netstat -tuln | grep ${APP1_PORT} || true", returnStatus: true) == 0
                    def isApp2Running = sh(script: "netstat -tuln | grep ${APP2_PORT} || true", returnStatus: true) == 0
                    def isApp3Running = sh(script: "netstat -tuln | grep ${APP3_PORT} || true", returnStatus: true) == 0

                    if (!isApp1Running) {
                        sh "cd ${PROJECT_DIR}/app1 && mvn spring-boot:run &"
                        sleep(time: 10, unit: 'SECONDS') // Ожидаем запуск
                    }
                    if (!isApp2Running) {
                        sh "cd ${PROJECT_DIR}/app2 && mvn spring-boot:run &"
                        sleep(time: 10, unit: 'SECONDS')
                    }
                    if (!isApp3Running) {
                        sh "cd ${PROJECT_DIR}/app3 && mvn spring-boot:run &"
                        sleep(time: 15, unit: 'SECONDS') // App3 может требовать больше времени
                    }
                }
            }
        }

        stage('Run E2E Tests') {
            steps {
                dir("${PROJECT_DIR}/app3") {
                    sh 'mvn test -Dtest=**FlowTest'
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
    }

    post {
        always {
            script {
                // Останавливаем приложения
                sh "pkill -f 'app1/target' || true"
                sh "pkill -f 'app2/target' || true"
                sh "pkill -f 'app3/target' || true"

                // Очистка
                sh "mvn clean -f ${PROJECT_DIR}/app1"
                sh "mvn clean -f ${PROJECT_DIR}/app2"
                sh "mvn clean -f ${PROJECT_DIR}/app3"
            }
        }
    }
}