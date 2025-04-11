package demo.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MessageService {

    public String processMessage(String message) {
        // Дополнительная бизнес-логика валидации
        if (!StringUtils.hasText(message)) {
            throw new IllegalArgumentException("Сообщение не может быть пустым");
        }

        if (message.length() > 1000) {
            throw new IllegalArgumentException("Сообщение слишком длинное");
        }

        // Обработка сообщения
        return "Сообщение принято: " + message;
    }

    public String getMessageStatus(Long id) {
        // Логика поиска сообщения
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Неверный ID сообщения");
        }

        return "Статус сообщения: PROCESSED";
    }
}