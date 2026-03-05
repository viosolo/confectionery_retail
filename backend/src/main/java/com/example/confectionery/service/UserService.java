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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserResponseMapper userResponseMapper;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userResponseMapper)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(userResponseMapper)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
    }

    @Transactional
    public UserResponse createUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
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
        return userResponseMapper.apply(savedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(id);
    }
}