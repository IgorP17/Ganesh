# Ganesh - Kafka Message Processing System

## System Architecture

HTTP Client → App1 (8080) → Kafka → App2 (8081) → Kafka → App3 (8082) → Web UI
Copy


## Core Components
| Service | Port | Description           |
|---------|------|-----------------------|
| App1    | 8080 | REST API Producer     |
| App2    | 8081 | Message Processor     |
| App3    | 8082 | Web UI + E2E Tests    |
| Kafka   | 9092 | Message Broker        |

## Quick Start
```bash
# Start infrastructure
docker-compose up -d

# Build and run
mvn clean package
java -jar app1/target/app1-*.jar &
java -jar app2/target/app2-*.jar &
java -jar app3/target/app3-*.jar &

# Run tests
mvn test -pl app3

Key Features

    End-to-End Testing with Selenide

    30% Flaky Test implementation

    Kafka message validation

Test Examples
java
Copy

// Flaky test
@Test
public void randomFailTest() {
    if (new Random().nextInt(100) < 30) {
        fail("Random failure (30% chance)");
    }
}

// E2E test
@Test
public void messageFlowTest() {
    await().atMost(30, SECONDS)
           .until(() -> $("#status").text().contains("SUCCESS"));
}

Jenkins Integration
groovy
Copy

pipeline {
    agent any
    stages {
        stage('Test') {
            steps {
                sh 'java -jar app3/target/*.jar &'
                sh 'mvn test -pl app3'
            }
        }
    }
}

Maintained by IgorP17
Copy


### Как использовать:
1. **Полностью скопируйте** этот текст (от `# Ganesh` до последней строки)
2. **Откройте файл README.md** в вашем проекте
3. **Удалите ВЕСЬ старый текст**
4. **Вставьте** этот новый текст
5. **Сохраните файл**

Этот файл:
- Содержит все ключевые компоненты вашего проекта
- Включает реальные команды и примеры кода
- Сохраняет простой markdown-формат
- Готов к немедленному использованию

