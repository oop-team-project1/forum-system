package com.company.web.forum.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class LoginDto {

    //@Email(message = "Email must be valid")

    @NotEmpty(message = "Email can't be empty!")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,6}$",
            message = "Please provide a valid email address. The email format should be like user@example.com"
    )
    private String email;
    @NotEmpty(message = "Password can't be empty.")
    private String password;

    public LoginDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
