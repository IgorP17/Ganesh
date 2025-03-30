package demo.e2e;

import com.codeborne.selenide.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class MessageFlowTest {
    private static final Logger logger = LoggerFactory.getLogger(MessageFlowTest.class);

    @Before
    public void setUp() {
        Configuration.browser = "firefox";
        Configuration.baseUrl = "http://localhost:8082";
        Configuration.timeout = 10000;
//        Configuration.headless = true; // Для запуска без GUI (опционально)
        Configuration.headless = false; // Для запуска без GUI (опционально)
    }

    @Test
    public void testMessageFlow() {
        open("/");
        $("#messageInput").shouldBe(visible);

        // 2. Отправляем сообщение
        LocalDateTime dt = LocalDateTime.now();
        String message = "Hello, autoQA! " + dt;
        String testMessage = "{\n" +
                "    \"message\":\"" + message + "\"\n" +
                "}";
        $("#messageInput").setValue(testMessage);
        $("#btnSend").shouldBe(visible); // Ждём появления кнопки
//        executeJavaScript("document.getElementById('btnSend').classList.remove('disabled')");
//        $("#btnSend").click();
        executeJavaScript("document.getElementById('btnSend').click()");
        // 3. Получаем ID из ответа
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
        $("#searchRequestButton").click();
//        $("#requestData").shouldHave(text(""));
//        $("#requestData").shouldHave(partialText(""));
        $("#requestData").should(matchText(
                "Request ID: " + stringID + ".*Message:.*" + message + ".*SUCCESS"
        ));

        // 5. Проверяем в разделе app2
        $("#requestIdProcessed").setValue(stringID);
        $("#searchProcessedRequestButton").click();
        $("#requestDataProcessed").should(matchText(
                "^ID: \\d+\n" +
                        "Request ID: " + stringID + "\n" +
                        "Data: \\{\"message\":\"" + Pattern.quote(message) + "\"\\}\n" +
                        "Status: SUCCESS\n" +
                        "Processed At: \\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+$"
        ));
    }
}