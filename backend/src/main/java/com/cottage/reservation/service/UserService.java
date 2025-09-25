package com.cottage.reservation.service;

import com.cottage.reservation.dto.SignUpRequest;
import com.cottage.reservation.entity.User;
import com.cottage.reservation.repository.UserRepository;
import com.cottage.reservation.service.InputValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private InputValidationService inputValidationService;

    public User createUser(SignUpRequest signUpRequest) {
        // Validate and sanitize input
        inputValidationService.validateBean(signUpRequest);
        
        // Sanitize text fields
        signUpRequest.setUsername(inputValidationService.validateAndSanitizeText(signUpRequest.getUsername()));
        signUpRequest.setFirstName(inputValidationService.validateAndSanitizeText(signUpRequest.getFirstName()));
        signUpRequest.setLastName(inputValidationService.validateAndSanitizeText(signUpRequest.getLastName()));
        signUpRequest.setEmail(inputValidationService.validateAndSanitizeEmail(signUpRequest.getEmail()));
        
        if (signUpRequest.getPhoneNumber() != null) {
            signUpRequest.setPhoneNumber(inputValidationService.validateAndSanitizeText(signUpRequest.getPhoneNumber()));
        }
        
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user
        User user = new User(signUpRequest.getUsername(),
                           signUpRequest.getEmail(),
                           passwordEncoder.encode(signUpRequest.getPassword()),
                           signUpRequest.getFirstName(),
                           signUpRequest.getLastName());
        
        user.setPhoneNumber(signUpRequest.getPhoneNumber());

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
