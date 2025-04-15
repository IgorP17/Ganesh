package demo.e2e;

import com.codeborne.selenide.Configuration;
import io.qameta.allure.*;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;

@Epic("E2E Тестирование потока сообщений")
@Feature("Негативные сценарии")
public class NegativeMessageFlowTest {
    private static final Logger logger = LoggerFactory.getLogger(NegativeMessageFlowTest.class);
    private static final String errorFront = "Ошибка: Неверный JSON формат";

    @BeforeClass
    public static void setup() {
        Configuration.browser = "firefox";
        Configuration.baseUrl = "http://localhost:8082";
        Configuration.timeout = 10000;
        Configuration.headless = true;
    }

    @Before
    public void openBrowser() {
        open("/");
    }

    @After
    public void tearDown() {
        closeWebDriver();
    }

    /*@Test
    @Story("Отправка некорректного сообщения")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Попытка отправить пустое сообщение")
    public void testEmptyMessageSubmission() {
        step("Отправляем пустое сообщение", () -> {
            $("#messageInput").setValue("");
            $("#btnSend").click();
        });

        step("Проверяем сообщение об ошибке", () -> {
            $("#response")
                    .shouldBe(visible)
                    .shouldHave(text(errorFront));
        });
    }

    @Test
    @Story("Отправка слишком длинного сообщения")
    @Severity(SeverityLevel.NORMAL)
    public void testLongMessageSubmission() {
        String longMessage = "A".repeat(1001);

        step("Отправляем слишком длинное сообщение", () -> {
            $("#messageInput").setValue(longMessage);
            $("#btnSend").click();
        });

        step("Проверяем сообщение об ошибке", () -> {
            $("#response")
                    .shouldBe(visible)
                    .shouldHave(text(errorFront));
        });
    }*/

    @Test
    @Story("Попытка поиска несуществующего ID")
    @Severity(SeverityLevel.MINOR)
    public void testNonExistentIdSearch() {
        String id = "9999999";
        step("Вводим несуществующий ID", () -> {
            $("#requestId").setValue(id);
            $("#searchRequestButton").click();
        });

        step("Проверяем сообщение об ошибке", () -> {
            $("#requestData")
                    .shouldBe(visible)
                    .shouldHave(text("Request not found for ID: " + id));
        });
    }

    @Test
    @Story("Попытка поиска несуществующего ID в requestDataProcessed")
    @Severity(SeverityLevel.MINOR)
    public void testNonExistentIdSearchProcessed() {
        String id = "9999999";
        step("Вводим несуществующий ID processed", () -> {
            $("#requestIdProcessed").setValue(id);
            $("#searchProcessedRequestButton").click();
        });

        step("Проверяем сообщение об ошибке", () -> {
            $("#requestDataProcessed")
                    .shouldBe(visible)
                    .shouldHave(text("No processed request found for requestId: " + id));
        });
    }
}