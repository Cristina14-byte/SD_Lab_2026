package com.chris.sd_assignment1.model.services;

import com.chris.sd_assignment1.model.entities.Role;
import com.chris.sd_assignment1.model.entities.User;
import com.chris.sd_assignment1.model.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.regex.Pattern;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String plainPassword, Role role, String email, String phone) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException("Invalid phone number.");
        }

        if (plainPassword == null || plainPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }

        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username already exists.");
        }

        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPasswordHash(hashedPassword);
        newUser.setRole(role);
        newUser.setEmail(email);
        newUser.setPhone(phone);

        return userRepository.save(newUser);
    }

    public Optional<User> authenticate(String username, String plainPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (BCrypt.checkpw(plainPassword, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return email != null && pattern.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        String phoneRegex = "^(\\+\\d+|\\d{10})$";
        Pattern pattern = Pattern.compile(phoneRegex);
        return phone != null && pattern.matcher(phone).matches();
    }
}