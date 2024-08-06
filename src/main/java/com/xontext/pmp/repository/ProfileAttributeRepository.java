package com.xontext.pmp.repository;

import com.xontext.pmp.model.Profile;
import com.xontext.pmp.model.ProfileAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileAttributeRepository extends JpaRepository<ProfileAttribute, Long> {
    void deleteByProfileIdAndAttributeId(Long profileId, Long attributeId);
    boolean existsByProfileIdAndAttributeId(Long profileId, Long id);
    List<ProfileAttribute> findByProfileAndCategory(Profile profile, String category);
    List<ProfileAttribute> findByProfileAndGroupId(Profile profile, Integer groupId);

    List<ProfileAttribute> findByProfileId(Long id);

    List<ProfileAttribute> findByProfileIdAndAttributeCategoryId(Long profileId, Long categoryId);
//    Profile findProfileByProfileAttributeId(Long profileAttributeId);
}
