package com.example.demo.Service;

import com.example.demo.model.Address;
import com.example.demo.model.User;
import com.example.demo.Repository.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate; // für die Sequence-Synchronisierung

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    /* =========================
       Registrierung
       ========================= */
    public User register(String email,
                         String username,
                         String password,
                         String street,
                         String houseNumber,
                         String postalCode,
                         String city) {

        // E-Mail bereits vergeben?
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("E-Mail already in use");
        }

        // Username bereits vergeben?
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already in use");
        }

        // Sequence synchronisieren (damit Fremdschlüssel später passt)
        // Sequence synchronisieren (damit Fremdschlüssel später passt)
        jdbcTemplate.execute(
                "SELECT setval('app_user_id_seq', COALESCE((SELECT MAX(id) FROM app_user), 0) + 1, false)"
        );



        // User erstellen
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        user = userRepository.save(user); // User bekommt jetzt eine gültige ID

        // Adresse erstellen
        Address address = new Address();
        address.setStreet(street);
        address.setHouseNumber(houseNumber);
        address.setPostalCode(postalCode);
        address.setCity(city);

        // Beziehung setzen
        address.setUser(user);
        user.setAddress(address);

        // Speichern (CascadeType.ALL sorgt dafür, dass Adresse automatisch gespeichert wird)
        return userRepository.save(user);
    }

    /* =========================
       Login
       ========================= */
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }
}
