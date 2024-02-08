package com.company.web.forum;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "TastyTale API", version = "1.0.0"))
@SecurityScheme(name = "basic", type = SecuritySchemeType.HTTP, scheme = "basic")
public class ForumApplication {
    public static void main(String[] args) {
        SpringApplication.run(ForumApplication.class, args);
    }

}
