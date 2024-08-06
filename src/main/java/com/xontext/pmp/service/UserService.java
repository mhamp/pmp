package com.xontext.pmp.service;

import com.xontext.pmp.model.User;
import com.xontext.pmp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService  {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public Optional<User> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }
    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }
    public User createUser(User user) {
        return userRepository.save(user);
    }
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User updateUser(Long id, User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActive(userDetails.isActive());
            return userRepository.save(user);
        }).orElse(null);
    }

}
