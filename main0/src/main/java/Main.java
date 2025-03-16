import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Hello, World! Now: {}", LocalDateTime.now());
    }

    public int sum(int a, int b) {
        return a + b;
    }
}