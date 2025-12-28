package com.example.demo.Controller;

import com.example.demo.Controller.dto.LoginRequest;
import com.example.demo.Controller.dto.RegistrationsRequest;
import com.example.demo.Service.UserService;
import com.example.demo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://10.19.23.228:3000") // Nuxt Dev
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /* =========================
       REGISTRATION
       ========================= */

    @PostMapping("/registration")
    public ResponseEntity<?> register(@RequestBody RegistrationsRequest request) {

        User user = userService.register(
                request.getEmail(),
                request.getUsername(),
                request.getPassword(),
                request.getStreet(),
                request.getHouseNumber(),
                request.getPostalCode(),
                request.getCity()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "username", user.getUsername()
                )
        );
    }

    /* =========================
       LOGIN
       ========================= */

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = userService.login(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(
                Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "username", user.getUsername()
                )
        );
    }
}
