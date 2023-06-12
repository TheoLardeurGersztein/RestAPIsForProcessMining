package org.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {
	
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        SpringApplication.run(Application.class, args);

    }

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

}