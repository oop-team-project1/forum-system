package com.company.web.forum.services;

import com.company.web.forum.exceptions.AuthorizationException;
import com.company.web.forum.exceptions.BlockedUnblockedUserException;
import com.company.web.forum.exceptions.EntityDuplicateException;
import com.company.web.forum.exceptions.EntityNotFoundException;
import com.company.web.forum.helpers.FilterOptionsUsers;
import com.company.web.forum.models.Post;
import com.company.web.forum.models.User;
import com.company.web.forum.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.company.web.forum.Helpers.*;
import static com.company.web.forum.Helpers.createMockUser;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;


    @Test
    public void getAll_Should_CallRepository()
    {
        FilterOptionsUsers mockFilterOptions = createMockFilterOptionsUsers();
        List<User> users = new ArrayList<>();

        Mockito.when(userRepository.getAll(mockFilterOptions)).thenReturn(users);

        Assertions.assertEquals(userRepository.getAll(mockFilterOptions), users);
    }
    @Test
    public void getById_Should_ReturnUser_When_MatchExists()
    {
        Mockito.when(userRepository.getById(1))
                .thenReturn(createMockUser());

        User result = userService.getById(1);

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("Mock", result.getUsername());
        Assertions.assertEquals("Mock", result.getPassword());
        Assertions.assertEquals("Mock", result.getFirstName());
        Assertions.assertEquals("Mock", result.getLastName());
        Assertions.assertEquals("Mock@mock.com", result.getEmail());
    }

    @Test
    public void getByName_Should_ReturnUser_When_MatchExists()
    {
        Mockito.when(userRepository.getByUsername(createMockUser().getUsername()))
                .thenReturn(createMockUser());

        User result = userService.getByUsername(createMockUser().getUsername());

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("Mock", result.getUsername());
        Assertions.assertEquals("Mock", result.getPassword());
        Assertions.assertEquals("Mock", result.getFirstName());
        Assertions.assertEquals("Mock", result.getLastName());
        Assertions.assertEquals("Mock@mock.com", result.getEmail());
    }

    @Test
    public void getByEmail_Should_ReturnUser_When_MatchExists()
    {
        Mockito.when(userRepository.getByEmail(createMockUser().getEmail()))
                .thenReturn(createMockUser());

        User result = userService.getByEmail(createMockUser().getEmail());

        Assertions.assertEquals(1, result.getId());
        Assertions.assertEquals("Mock", result.getUsername());
        Assertions.assertEquals("Mock", result.getPassword());
        Assertions.assertEquals("Mock", result.getFirstName());
        Assertions.assertEquals("Mock", result.getLastName());
        Assertions.assertEquals("Mock@mock.com", result.getEmail());
    }

    @Test
    public void create_Should_CallRepository_When_UserDoesNotExists()
    {
        User mockUser = createMockUser();

        Mockito.when(userRepository.getByUsername(mockUser.getUsername()))
                .thenThrow(new EntityNotFoundException("User", "username", mockUser.getUsername()));

        userService.create(mockUser);

        Mockito.verify(userRepository, Mockito.times(1))
                .create(mockUser);
    }

    @Test
    public void create_Should_Throw_When_UserWithSameUsernameExists()
    {
        User mockUser = createMockUser();

        Mockito.when(userRepository.getByUsername(mockUser.getUsername()))
                .thenReturn(mockUser);

        Assertions.assertThrows(EntityDuplicateException.class,
                () -> userService.create(mockUser));
    }

    @Test
    public void update_Should_CallRepository_When_UserDoesNotExists()
    {
        User mockUser = createMockUser();

        Mockito.when(userRepository.getByUsername(mockUser.getUsername()))
                .thenThrow(new EntityNotFoundException("User", "username", mockUser.getUsername()));

        userService.update(mockUser, createMockUser());

        Mockito.verify(userRepository, Mockito.times(1))
                .update(mockUser);
    }

    @Test
    public void update_Should_Throw_When_UserWithSameUsernameExists()
    {
        User mockUser = createMockUser();
        User mockUser2 = createMockUser();
        mockUser2.setId(2);

        Mockito.when(userRepository.getByUsername(mockUser.getUsername()))
                .thenReturn(mockUser2);

        Assertions.assertThrows(EntityDuplicateException.class,
                () -> userService.update(mockUser, createMockUser()));
    }
    @Test
    public void update_Should_Throw_When_UserIsBlocked()
    {
        User mockUser = createMockUser();
        mockUser.setBlocked(true);
        User mockUser2 = createMockUser();
        mockUser2.setId(2);

        Assertions.assertThrows(AuthorizationException.class,
                () -> userService.update(mockUser2, mockUser));
    }

}
