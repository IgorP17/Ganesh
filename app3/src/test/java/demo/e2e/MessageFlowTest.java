package demo.e2e;

import com.codeborne.selenide.Configuration;
import io.qameta.allure.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.*;
import static org.awaitility.Awaitility.await;

@Epic("E2E Тестирование потока сообщений")
@Feature("Основной поток обработки сообщений")
public class MessageFlowTest {
    private static final Logger logger = LoggerFactory.getLogger(MessageFlowTest.class);

    @Before
    @Step("Настройка окружения")
    public void setUp() {
        logger.info("TEST {}", "EE");
        Configuration.browser = "firefox";
        Configuration.baseUrl = "http://localhost:8082";
        Configuration.timeout = 10000;
        Configuration.headless = true;
        open("http://localhost:8082");
    }

    @After
    @Step("Очистка после теста")
    public void tearDown() {
        closeWebDriver();
    }

    @Test
    @Story("Пользователь отправляет сообщение через систему")
    @Description("Проверка полного цикла обработки сообщения через все микросервисы")
    @Severity(SeverityLevel.CRITICAL)
    public void testMessageFlow() {
        step("Проверка видимости поля ввода", () -> {
            $("#messageInput").shouldBe(visible);
        });

        LocalDateTime dt = LocalDateTime.now();
        String message = "Hello, autoQA! " + dt;
        String testMessage = "{\"message\":\"" + message + "\"}";

        step("Отправка тестового сообщения: " + message, () -> {
            $("#messageInput").setValue(testMessage);
            $("#btnSend").click();
        });

        String response = step("Получение ID сообщения", () -> {
            $("#response")
                    .shouldBe(visible, ofSeconds(10))
                    .shouldHave(matchText("Request saved with ID: \\d+"));
            return $("#response").text();
        });

        String stringID = step("Извлечение ID из ответа", () -> {
            Pattern pattern = Pattern.compile("Request saved with ID: (\\d+)");
            Matcher matcher = pattern.matcher(response);
            if (matcher.find()) {
                return matcher.group(1);
            }
            Assert.fail("ID not found in text: " + response);
            return "";
        });

        step("Проверка статуса в app1", () -> {
            $("#requestId").setValue(stringID);
            await().atMost(90, SECONDS)
                    .pollInterval(5, SECONDS)
                    .pollInSameThread()
                    .untilAsserted(() -> {
                        $("#searchRequestButton").click();
                        $("#requestData").shouldHave(text("SUCCESS"));
                    });
        });

        step("Проверка данных в app2", () -> {
            $("#requestIdProcessed").setValue(stringID);
            $("#searchProcessedRequestButton").click();
            $("#requestDataProcessed")
                    .shouldBe(visible, Duration.ofSeconds(30))
                    .should(matchText(
                            "^ID: \\d+\n" +
                                    "Request ID: " + stringID + "\n" +
                                    "Data: \\{\"message\":\"" + Pattern.quote(message) + "\"\\}\n" +
                                    "Status: SUCCESS\n" +
                                    "Processed At: \\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+$"
                    ));
        });
    }

    @Test
    @Story("Тестовые сценарии")
    @Description("Всегда успешный тест для проверки инфраструктуры")
    @Severity(SeverityLevel.MINOR)
    public void testFake() {
        step("Проверка тривиального утверждения", () -> {
            Assert.assertEquals(1L, 1L);
        });
    }

    @Test
    @Story("Тестовые сценарии")
    @Description("Тест с возможностью случайного падения")
    @Severity(SeverityLevel.NORMAL)
    public void testFakeRandomFailure() {
        Random random = new Random(System.currentTimeMillis());
        int chance = random.nextInt(100);

        step("Генерация случайного числа: " + chance, () -> {
            if (chance < 0) {
                Assert.fail("Имитация случайного падения теста (вероятность 0%)");
            }
        });
    }
}