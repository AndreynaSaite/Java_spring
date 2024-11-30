package org.example.authservice.controller;

import org.example.authservice.dto.RegisterRequest;
import org.example.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class AuthController {
    @Autowired
    private AuthService authService;

    // Редирект на страницу регистрации по умолчанию
    @GetMapping("/")
    public String home() {
        return "redirect:/register";  // Перенаправление на страницу регистрации
    }
    // Метод для отображения формы регистрации
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";  // Имя шаблона для страницы регистрации
    }

    // Метод для обработки данных формы регистрации
    @PostMapping("/register")
    public String register(RegisterRequest registerRequest) {
        try {
            authService.register(registerRequest);
            return "redirect:/registration-success";  // После успешной регистрации переходим на страницу логина
        } catch (Exception e) {
            return "redirect:/registration-failed";  // В случае ошибки возвращаем на страницу регистрации
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Отдаём шаблон login.html
    }

    // Метод для обработки логина
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        try {
            authService.authenticate(email, password);
            return "redirect:/registration-success";  // Перенаправление на домашнюю страницу после успешного логина
        } catch (Exception e) {
            return "registration-failed";  // Ошибка логина, возвращаемся на страницу логина
        }
    }

    @GetMapping("/registration-success")
    public String registrationSuccess() {
        return "registration-success";
    }

    @GetMapping("/registration-failed")
    public String registrationFailed() {
        return "registration-failed";
    }

}
