package com.company.web.forum.helpers;

import com.company.web.forum.models.RegisterDto;
import com.company.web.forum.models.User;
import com.company.web.forum.models.UserDtoCreation;
import com.company.web.forum.models.UserDtoUpdating;
import com.company.web.forum.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final UserService userService;

    @Autowired
    public UserMapper(UserService userService) {
        this.userService = userService;
    }

    public User fromDtoCreation(int id, UserDtoCreation userDto) {
        User user = fromDtoCreation(userDto);
        user.setUsername(userService.getById(id).getUsername());
        user.setId(id);
        user.setAdmin(false);
        return user;
    }

    public User fromDtoCreation(UserDtoCreation userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        return user;
    }

    public User fromDtoUpdating(int id, UserDtoUpdating userDto) {
        User user = fromDtoUpdating(userDto);
        user.setId(id);
        user.setAdmin(false);
        return user;
    }
    public User fromDtoUpdating(UserDtoUpdating userDto) {
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        return user;
    }

    public User fromDtoUpdating(UserDtoUpdating userDto, User user) {
        User userToUpdate = fromDtoUpdating(userDto);
        userToUpdate.setId(user.getId());
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setBlocked(user.isBlocked());
        userToUpdate.setAdmin(user.isAdmin());
        userToUpdate.setPostsByUser(user.getPostsByUser());
        userToUpdate.setProfilePic(user.getProfilePic());
        return userToUpdate;
    }

    public User fromDtoRegister(RegisterDto registerDto) {
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setPassword(registerDto.getPassword());
        user.setEmail(registerDto.getEmail());
        return user;
    }
}
