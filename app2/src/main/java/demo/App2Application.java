package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"demo", "demo.controller"})
public class App2Application {
    public static void main(String[] args) {
        SpringApplication.run(App2Application.class, args);
    }
}
