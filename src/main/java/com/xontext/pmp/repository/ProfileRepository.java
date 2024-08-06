package com.xontext.pmp.repository;

import com.xontext.pmp.model.Profile;
import com.xontext.pmp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findProfilesByUser(User user);
    boolean existsByUserIdAndProfileName(Long userId, String profileName);
    void deleteProfilesByUserId(Long id);
}
