package com.xontext.pmp.controller;

import com.xontext.pmp.model.Profile;
import com.xontext.pmp.model.User;
import com.xontext.pmp.service.core.ProfileService;
import com.xontext.pmp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;
    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody User user){
        try{
            if(userService.getUserByUsername(user.getUsername()).isPresent()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists. Try again with another name.");
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User save = userService.createUser(user);
            Profile profile = Profile.builder()
                    .user(user)
                    .profileName(user.getUsername())
                    .build();
            profile = profileService.createProfile(user.getId(), profile);
            return ResponseEntity.ok(HttpStatus.CREATED);
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
    @PutMapping("/{id}")
    public ResponseEntity updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            if (userService.getUserById(id) != null) {
                User updatedUser = userService.updateUser(id, userDetails);
                if (updatedUser != null) {
                    return new ResponseEntity<>(updatedUser, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        return null;
    }

    /*
     * TODO: java.sql.SQLIntegrityConstraintViolationException: Cannot delete or update a parent row:
     * a foreign key constraint fails (`pmp`.`profile`, CONSTRAINT `FKawh070wpue34wqvytjqr4hj5e` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`))
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}