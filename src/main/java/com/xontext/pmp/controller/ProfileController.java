package com.xontext.pmp.controller;

import com.xontext.pmp.model.Profile;
import com.xontext.pmp.repository.ProfileRepository;
import com.xontext.pmp.service.core.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
@AllArgsConstructor
public class ProfileController {

    private final ProfileRepository profileRepository;
    private final ProfileService profileService;

    @PostMapping("/users/{userId}/profiles")
    public ResponseEntity createProfile(@PathVariable Long userId, @RequestBody Profile profile) {
        try {
            if (profileService.profileNameExistsForUser(userId, profile.getProfileName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Profile with this name already exists for the user.");
            } else {
                Profile createdProfile = profileService.createProfile(userId, profile);
                return ResponseEntity.ok(createdProfile);
            }
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    @GetMapping("/users/{userId}/profiles")
    public ResponseEntity<List<Profile>> getAllProfilesByUserId(@PathVariable Long userId) {
        List<Profile> profiles = profileService.getAllProfilesForUser(userId);
        return ResponseEntity.ok(profiles);
    }
    @GetMapping("/profiles/{id}")
    public ResponseEntity<Profile> getProfileById(@PathVariable Long id) {
        Profile profile = profileService.getProfileById(id);
        return ResponseEntity.ok(profile);
    }
    @PutMapping("/profiles/{id}")
    public ResponseEntity<Profile> updateProfile(@PathVariable Long id, @RequestBody Profile profile) {
        Profile updatedProfile = profileService.updateProfile(id, profile);
        return ResponseEntity.ok(updatedProfile);
    }
    @DeleteMapping("/profiles/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        profileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/users/{id}/profiles")
    public ResponseEntity<Void> deleteProfilesByUserId(@PathVariable Long id) {
        profileService.deleteProfileByUserId(id);
        return ResponseEntity.noContent().build();
    }
}