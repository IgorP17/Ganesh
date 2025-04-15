package demo.service;

import demo.model.Request;
import demo.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    // Сохраняем запрос в базу данных
    public Long saveRequest(String message) {
        Request request = new Request();
        request.setData(message);
        request.setStatus("PENDING"); // Устанавливаем начальный статус
        requestRepository.save(request);
        return request.getId(); // Возвращаем ID сохраненного запроса
    }

    // Ищем запрос по ID
    public String getRequestData(Long id) {
        Optional<Request> requestOptional = requestRepository.findById(id);

        if (requestOptional.isPresent()) {
            Request request = requestOptional.get();
            return "Request ID: " + request.getId() + "\n" +
                    "Message: " + request.getData() + "\n" +
                    "Status: " + request.getStatus();
        } else {
            return "Request not found for ID: " + id;
        }
    }
}