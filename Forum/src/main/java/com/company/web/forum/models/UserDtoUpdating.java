package com.company.web.forum.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDtoUpdating {
    @NotEmpty(message = "First name can't be empty")
    @Size(min = 4, max = 32, message = "The first name must be between 4 symbols and 32 symbols.")
    private String firstName;
    @NotEmpty(message = "Last name can't be empty")
    @Size(min = 4, max = 32, message = "The last name must be between 4 symbols and 32 symbols.")
    private String lastName;
    @NotEmpty(message = "Password can't be empty")
    private String password;
    @NotEmpty(message = "Email can't be empty")
    private String email;
    private String phoneNumber;

    public UserDtoUpdating() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
