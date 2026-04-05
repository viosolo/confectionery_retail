package com.example.confectionery.service;

import com.example.confectionery.dto.LoginRequest;
import com.example.confectionery.dto.UserRegisterRequest;
import com.example.confectionery.dto.UserResponse;
import com.example.confectionery.entity.Role;
import com.example.confectionery.entity.User;
import com.example.confectionery.exception.BadRequestException;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.exception.UserAlreadyExistsException;
import com.example.confectionery.mapper.UserResponseMapper;
import com.example.confectionery.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserResponseMapper userResponseMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("getAllUsers - Success")
    void getAllUsers_Success() {
        User user = new User();
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userResponseMapper.apply(any())).thenReturn(new UserResponse());

        List<UserResponse> result = userService.getAllUsers();

        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("login - Success")
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("correct_password");

        User user = User.builder()
                .email("test@mail.com")
                .password("correct_password")
                .build();

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));
        when(userResponseMapper.apply(user)).thenReturn(new UserResponse());

        UserResponse result = userService.login(request);

        assertNotNull(result);
        verify(userRepository).findByEmail("test@mail.com");
    }

    @Test
    @DisplayName("login - User Not Found")
    void login_UserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@mail.com");

        when(userRepository.findByEmail("unknown@mail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.login(request));
        verify(userResponseMapper, never()).apply(any());
    }

    @Test
    @DisplayName("login - Invalid Password")
    void login_InvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("wrong_password");

        User user = User.builder()
                .email("test@mail.com")
                .password("correct_password")
                .build();

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> userService.login(request));
        verify(userResponseMapper, never()).apply(any());
    }

    @Test
    @DisplayName("getUserById - Success")
    void getUserById_Success() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userResponseMapper.apply(user)).thenReturn(new UserResponse());

        UserResponse result = userService.getUserById(1L);

        assertNotNull(result);
    }

    @Test
    @DisplayName("getUserById - NotFound")
    void getUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    @DisplayName("createUser - Success")
    void createUser_Success() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("test@mail.com");
        request.setFirstName("Ivan");

        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .role(Role.USER)
                .build();

        when(userRepository.existsByEmail("test@mail.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userResponseMapper.apply(any(User.class))).thenReturn(new UserResponse());

        UserResponse result = userService.createUser(request);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("createUser - AlreadyExistsException")
    void createUser_EmailExists() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("existing@mail.com");

        when(userRepository.existsByEmail("existing@mail.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteUser - Success")
    void deleteUser_Success() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("deleteUser - NotFound")
    void deleteUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).delete(any());
    }
}