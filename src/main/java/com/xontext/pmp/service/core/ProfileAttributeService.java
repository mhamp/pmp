package com.xontext.pmp.service.core;

import com.xontext.pmp.dto.ProfileAttributeValueDTO;
import com.xontext.pmp.model.Attribute;
import com.xontext.pmp.model.Profile;
import com.xontext.pmp.model.ProfileAttribute;
import com.xontext.pmp.repository.ProfileAttributeRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
@AllArgsConstructor
public class ProfileAttributeService {
    private final ProfileService profileService;
    private final CategoryService categoryService;
    private final AttributeService attributeService;
    private final ProfileAttributeRepository profileAttributeRepository;
    @Transactional
    public void addOrUpdateAttribute(Profile profile, Long attributeId, ProfileAttributeValueDTO profileAttributeValueDTO) {
        // Logic to add or update an attribute
        Attribute attribute = attributeService.getAttributeById(attributeId);
        // Ensure mandatory categories have at least one attribute set
        if (attribute.isRequired() && profileAttributeValueDTO.getValue() == null) {
            throw new IllegalArgumentException("Attribute value can't be null. Attribute requires a value.");
        }
        ProfileAttribute profileAttribute = new ProfileAttribute();
        profileAttribute.setProfile(profile);
        profileAttribute.setAttribute(profileAttributeValueDTO.getAttribute());
        // Set the appropriate value based on the attribute type
        switch (attribute.getValueType()) {
            case STRING -> profileAttribute.setStringValue(profileAttributeValueDTO.getStringValue());
            case INTEGER -> profileAttribute.setIntValue(profileAttributeValueDTO.getIntValue());
            case FLOAT -> profileAttribute.setFloatValue(profileAttributeValueDTO.getFloatValue());
            case BOOLEAN -> profileAttribute.setBoolValue(profileAttributeValueDTO.getBoolValue());
            case DATE -> profileAttribute.setDateValue(profileAttributeValueDTO.getDateValue());
        }
        profileAttributeRepository.save(profileAttribute);
    }

    public void setAttributes(List<ProfileAttributeValueDTO> attributes){
        for (ProfileAttributeValueDTO attribute : attributes){
            ProfileAttribute profileAttribute = ProfileAttribute.builder()
                    .attribute(attribute.getAttribute())
                    .profile(profileService.getProfileById(attribute.getProfileId()))
                    .build();
            profileAttributeRepository.save(profileAttribute);
        }
    }

    private void setProfileAttributeValue(ProfileAttribute profileAttribute, Object value) {
        switch (profileAttribute.getAttribute().getValueType()) {
            case STRING -> profileAttribute.setStringValue((String) value);
            case INTEGER -> profileAttribute.setIntValue((Integer) value);
            case FLOAT -> profileAttribute.setFloatValue((Float) value);
            case BOOLEAN -> profileAttribute.setBoolValue((Boolean) value);
            case DATE -> profileAttribute.setDateValue((Date) value);
            default -> throw new IllegalArgumentException("Invalid value type");
        }
    }

//    public void deleteProfileAttribute(Long profileAttributeId) {
//        Long profileId = getProfileByProfileAttributeId(profileAttributeId).getId();
//        profileAttributeRepository.deleteByProfileIdAndAttributeId(profileId, profileAttributeId);
//    }

    public void deleteAttributesByCategory(Long profileId, Long categoryId) {
        List<Attribute> attributes = attributeService.getAllAttributesByCategoryId(categoryId);
        for (Attribute attribute : attributes) {
            profileAttributeRepository.deleteByProfileIdAndAttributeId(profileId, attribute.getId());
        }
    }
//    public Profile getProfileByProfileAttributeId(Long profileAttributeId){
//        return profileAttributeRepository.findProfileByProfileAttributeId(profileAttributeId);
//    }
    public List<ProfileAttribute> getAttributeByProfileIdAndCategoryId(Long profileId, Long categoryId) {
        return profileAttributeRepository.findByProfileIdAndAttributeCategoryId(profileId, categoryId);
    }

    public List<ProfileAttribute> getAttributesByGroup(Long profileId, int groupId) {
        Profile profile = profileService.getProfileById(profileId);
        return profileAttributeRepository.findByProfileAndGroupId(profile, groupId);
    }

    public List<ProfileAttribute> getAttributes(Long id) {
        return profileAttributeRepository.findByProfileId(id);
    }

//    public void validateMandatoryCategories(Long profileId) {
//        List<Category> mandatoryCategories = categoryService.findByIsMandatoryTrue();
//        for (Category category : mandatoryCategories) {
//            List<Attribute> attributes = attributeService.getAllAttributesByCategoryId(category.getId());
//            boolean hasAttribute = attributes.stream().anyMatch(attribute ->
//                    profileAttributeRepository.existsByProfileIdAndAttributeId(profileId, attribute.getId())
//            );
//            if (!hasAttribute) {
//                throw new IllegalArgumentException("Profile must have at least one attribute set in mandatory category " + category.getName());
//            }
//        }
//    }
}
