package org.example.authservice.service;

import org.example.authservice.dto.RegisterRequest;
import org.example.authservice.model.User;
import org.example.authservice.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File jsonFile = new File("users.json");

    public void register(RegisterRequest registerRequest) throws IOException {
        // Проверяем, существует ли пользователь с таким email
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());

        // Добавление пользователя в JSON
        List<User> users = loadUsersFromJson();
        users.add(user);
        objectMapper.writeValue(jsonFile, users);

        // Сохранение в БД
        userRepository.save(user);
    }

    private List<User> loadUsersFromJson() throws IOException {
        // Если файл не существует, возвращаем пустой список
        if (!jsonFile.exists()) {
            return new ArrayList<>();
        }

        // Загружаем список пользователей из JSON
        try {
            return objectMapper.readValue(jsonFile, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            // Если файл повреждён, возвращаем пустой список
            return new ArrayList<>();
        }
    }

    public User authenticate(String email, String password) {
        // Проверяем, существует ли пользователь с данным email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // Проверяем пароль с учётом возможных пробелов или ошибок
        if (password == null || !password.trim().equals(user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Если всё успешно, возвращаем пользователя
        return user;
    }

}
