package demo.e2e;

import com.codeborne.selenide.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class MessageFlowTest {

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
        String testMessage = "{\n" +
                "    \"message\": \"Hello, autoQA! " + dt + "\"\n" +
                "}";
        $("#messageInput").setValue(testMessage);
        $("#btnSend").click();
/*
        // 3. Получаем ID из ответа
        String requestId = $("#requestId").shouldBe(visible).text();

        // 4. Проверяем в разделе app1
        $("#searchRequestId").setValue(requestId);
        $("#searchRequestButton").click();
        $("#requestResult").shouldHave(text(testMessage));

        // 5. Проверяем в разделе app2
        $("#searchProcessedRequestId").setValue(requestId);
        $("#searchProcessedRequestButton").click();
        $("#processedRequestResult").shouldHave(text(testMessage));
    }
    */
    }
}