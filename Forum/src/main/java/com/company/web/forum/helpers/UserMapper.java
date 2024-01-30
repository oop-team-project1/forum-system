package com.company.web.forum.helpers;

import com.company.web.forum.models.User;
import com.company.web.forum.models.UserDto;
import com.company.web.forum.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper
{
    private final UserService userService;

    @Autowired
    public UserMapper(UserService userService)
    {
        this.userService = userService;
    }

    public User fromDto(int id, UserDto userDto)
    {
        User user = fromDto(userDto);
        user.setId(id);
        user.setAdmin(false);
        return user;
    }

    public User fromDto(UserDto userDto)
    {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        if (userDto.isValidEmail()) {
            user.setEmail(userDto.getEmail());
        }else {
            //TODO what to do if the email is not valid
        }
        user.setPhoneNumber(userDto.getPhoneNumber());
        return user;
    }
}
