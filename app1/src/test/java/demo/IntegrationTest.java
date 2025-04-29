package demo;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationTest.class);
    private static final String BASE_URL = "http://localhost:8080"; // Адрес, на котором запущено приложение
    private static JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        // Настройка соединения с базой данных
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver"); // Если H2, иначе подставь драйвер твоей БД
        dataSource.setUrl("jdbc:postgresql://localhost:5432/app_db"); // Подставь правильный URL
        dataSource.setUsername("app_user");
        dataSource.setPassword("password");

        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void testSendAndCheckInDatabase() {
        // Arrange
        String messageToSend = "Hello, app1! " + LocalDateTime.now();
        // Составляем JSON с помощью string template
        String jsonMessage = """
                {"message": "%s"}""".formatted(messageToSend);

        logger.info("Send message via REST: {}", jsonMessage);

        // Act: Отправляем сообщение через REST API
        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(jsonMessage)
                .post(BASE_URL + "/api/send");

        // Assert: Проверяем ответ
        logger.info("Got response {}", response.asString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.asString()).contains("Request saved with ID:");
        String stringID = step("Извлечение ID из ответа", () -> {
            Pattern pattern = Pattern.compile("Request saved with ID: (\\d+)");
            Matcher matcher = pattern.matcher(response.asString());
            if (matcher.find()) {
                return matcher.group(1);
            }
            Assert.fail("ID not found in text: " + response);
            return "";
        });
        Long id = Long.parseLong(stringID);
        logger.info("Extracted id = {}", id);

        // Act: Проверяем наличие записи в базе данных
        List<Map<String, Object>> results = jdbcTemplate.queryForList("SELECT * FROM REQUEST WHERE ID = ?",
               id);

        // Assert: Проверяем наличие записи в базе и её статус
        assertThat(results).hasSize(1);
        logger.info("Got from DB = {}", results);
        assertThat(results.get(0)).containsEntry("ID", id);
        assertThat(results.get(0)).containsEntry("STATUS", "PENDING");
        assertThat(results.get(0)).containsEntry("DATA", jsonMessage);
    }
}