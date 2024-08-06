package com.xontext.pmp.service.core;


import com.xontext.pmp.model.Profile;
import com.xontext.pmp.model.User;
import com.xontext.pmp.repository.ProfileRepository;
import com.xontext.pmp.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public Profile createProfile(Long userId, Profile profile) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        profile.setUser(user);
        return profileRepository.save(profile);
    }

    public boolean profileNameExistsForUser(Long userId, String profileName) {
        return profileRepository.existsByUserIdAndProfileName(userId, profileName);
    }

    public List<Profile> getAllProfilesForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return profileRepository.findProfilesByUser(user);
    }

    public Profile getProfileById(Long profileId) {
        return profileRepository.findById(profileId).orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    public Profile updateProfile(Long profileId, Profile profile) {
        Profile existingProfile = profileRepository.findById(profileId).orElseThrow(() -> new RuntimeException("Profile not found"));
        existingProfile.setProfileName(profile.getProfileName());
        // Update other fields as necessary
        return profileRepository.save(existingProfile);
    }

    public void deleteProfile(Long profileId) {
        profileRepository.deleteById(profileId);
    }

    public void deleteProfileByUserId(Long id) {
        profileRepository.deleteProfilesByUserId(id);
    }
}
