package demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/processed-requests/**") // Разрешаем CORS для всех эндпоинтов /api
                .allowedOrigins("http://localhost:8082") // Разрешаем запросы от app3
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Разрешаем методы
                .allowedHeaders("*") // Разрешаем все заголовки
                .allowCredentials(true); // Разрешаем передачу куки и авторизационных данных
    }
}

// CORS чтобы разрешить