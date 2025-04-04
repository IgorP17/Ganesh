package demo.e2e;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
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
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.*;
import static org.awaitility.Awaitility.await;

public class MessageFlowTest {
    private static final Logger logger = LoggerFactory.getLogger(MessageFlowTest.class);

    @Before
    public void setUp() {
        Configuration.browser = "firefox";
        Configuration.baseUrl = "http://localhost:8082";
        Configuration.timeout = 10000;
        Configuration.headless = true; // Для запуска без GUI (опционально)
//        Configuration.headless = false; // Для запуска c GUI (опционально)
        // Открываем страницу перед каждым тестом
        open("http://localhost:8082");
    }

    @After
    public void tearDown() {
        // Закрываем браузер после теста
        closeWebDriver();
    }

    @Test
    public void testMessageFlow() {
        $("#messageInput").shouldBe(visible);

        // 2. Отправляем сообщение
        LocalDateTime dt = LocalDateTime.now();
        String message = "Hello, autoQA! " + dt;
        String testMessage = "{\n" +
                "    \"message\":\"" + message + "\"\n" +
                "}";
        $("#messageInput").setValue(testMessage);
        $("#btnSend").shouldBe(visible).click();
//        executeJavaScript("document.getElementById('btnSend').classList.remove('disabled')");
//        $("#btnSend").click();
//        executeJavaScript("document.getElementById('btnSend').click()");

        // 3. Получаем ID из ответа
        $("#response")
                .shouldBe(visible, ofSeconds(10))
                .shouldHave(matchText(
                        "Request saved with ID: \\d+"
                ));
        String response = $("#response").shouldBe(visible).text();
        logger.info("Got request: {}", response);
        // 3.1. Создаем Pattern с регулярным выражением
        Pattern pattern = Pattern.compile("Request saved with ID: (\\d+)");

        // 3.2. Создаем Matcher для входной строки
        Matcher matcher = pattern.matcher(response);

        // 3.3. Проверяем совпадение и извлекаем ID
        String stringID = "";
        if (matcher.find()) {
            stringID = matcher.group(1); // Группа 1 - то, что в скобках
            logger.info("Extracted ID: {}", stringID); // Выведет "Extracted ID: 49"
        } else {
            Assert.fail("ID not found in text: " + response);
        }


        // 4. Проверяем в разделе app1
        $("#requestId").setValue(stringID);
//        $("#searchRequestButton").click();
//        $("#requestData").shouldHave(text(""));
//        $("#requestData").shouldHave(partialText(""));

        // ждем сукеса
        // 3. Ожидаем SUCCESS статус с Awaitility
        await().atMost(90, SECONDS)
                .pollInterval(5, SECONDS)
                .pollInSameThread() // Ключевая настройка!
                .untilAsserted(() -> {
                    $("#searchRequestButton").click();
                    $("#requestData").shouldHave(text("SUCCESS"));
                });

        $("#requestData")
                .shouldBe(visible, Duration.ofSeconds(30))
                .should(matchText(
                        "Request ID: " + stringID + ".*Message:.*" + message + ".*SUCCESS"
                ));

        // 5. Проверяем в разделе app2
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
    }

    @Test
    public void testFake() {
        Assert.assertEquals("FAKE", 1L, 1L);
    }

    @Test
    public void testFake2() {
        Assert.assertEquals("FAKE2", 2L, 2L);
    }

    @Test
    public void testFakeRandomFailure() {
        Random random = new Random(System.currentTimeMillis());
        int chance = random.nextInt(100);
        logger.info("testFakeRandomFailure - Random chance: " + chance + "/100");

        if (chance < 30) {
            Assert.fail("Имитация случайного падения теста (вероятность 30%)");
        }
    }
}