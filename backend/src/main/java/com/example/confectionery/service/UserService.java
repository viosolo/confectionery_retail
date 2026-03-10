package com.example.confectionery.service;

import com.example.confectionery.dto.UserRegisterRequest;
import com.example.confectionery.dto.UserResponse;
import com.example.confectionery.entity.Role;
import com.example.confectionery.entity.User;
import com.example.confectionery.exception.ResourceNotFoundException;
import com.example.confectionery.exception.UserAlreadyExistsException;
import com.example.confectionery.mapper.UserResponseMapper;
import com.example.confectionery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserResponseMapper userResponseMapper;

    private static final String LOG_ERROR_NOT_FOUND = ">>> {} failed: User ID {} not found";
    private static final String USER_NOT_FOUND_MSG = "Пользователь не найден";

    public List<UserResponse> getAllUsers() {
        log.info(">>> Fetching all users list");
        return userRepository.findAll()
                .stream()
                .map(userResponseMapper)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        User user = findUserOrThrow(id, "Search");
        return userResponseMapper.apply(user);
    }

    @Transactional
    public UserResponse createUser(UserRegisterRequest request) {
        log.info(">>> Attempting to create user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn(">>> Registration failed: email {} already exists", request.getEmail());
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
        }

        User userEntity = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phone(request.getPhone())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(userEntity);
        log.info(">>> User successfully registered with ID: {}", savedUser.getId());

        return userResponseMapper.apply(savedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info(">>> Attempting to delete user ID: {}", id);

        // Используем тот же метод поиска, что и в getUserById
        User user = findUserOrThrow(id, "Deletion");

        userRepository.delete(user);
        log.info(">>> User ID {} deleted successfully", id);
    }

    private User findUserOrThrow(Long id, String actionName) {
        return userRepository.findById(id)
                .orElseThrow(() -> {

                    log.error(LOG_ERROR_NOT_FOUND, actionName, id);
                    return new ResourceNotFoundException(USER_NOT_FOUND_MSG);
                });
    }
}