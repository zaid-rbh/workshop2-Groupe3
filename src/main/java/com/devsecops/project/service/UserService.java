package com.devsecops.project.service;

import com.devsecops.project.dto.RegisterForm;
import com.devsecops.project.model.User;
import com.devsecops.project.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterForm form) {
        // Ni le mot de passe ni l'email ne sont écrits dans les logs.
        log.info("New registration: username={}", form.getUsername());

        User user = new User();
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setFullName(form.getFullName());
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        user.setRole("USER");
        return userRepository.save(user);
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("No user named " + username));
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No user with id " + id));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public long count() {
        return userRepository.count();
    }
}
