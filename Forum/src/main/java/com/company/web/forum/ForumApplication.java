package com.company.web.forum;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "TastyTale API", version = "1.0.0"))
public class ForumApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(ForumApplication.class, args);
    }

}
