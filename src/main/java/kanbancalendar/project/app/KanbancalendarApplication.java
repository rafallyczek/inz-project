package kanbancalendar.project.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KanbancalendarApplication {

    public static void main(String[] args) {
        SpringApplication.run(KanbancalendarApplication.class, args);
    }

}
