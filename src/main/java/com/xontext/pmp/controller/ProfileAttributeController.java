package com.xontext.pmp.controller;

import com.xontext.pmp.dto.ProfileAttributeValueDTO;
import com.xontext.pmp.model.Profile;
import com.xontext.pmp.model.ProfileAttribute;
import com.xontext.pmp.service.core.ProfileAttributeService;
import com.xontext.pmp.service.core.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/attributes")
@AllArgsConstructor
public class ProfileAttributeController {

    private final ProfileAttributeService profileAttributeService;
    private final ProfileService profileService;

    @PostMapping("/{attributeId}")
    public ResponseEntity addProfileAttribute(@PathVariable Long profileId, @PathVariable Long attributeId, @RequestBody ProfileAttributeValueDTO profileAttributeValueDTO){
        try {
            Profile profile = profileService.getProfileById(profileId);
            if (profile == null) {
                return ResponseEntity.notFound().build();
            }

            ProfileAttribute profileAttribute = new ProfileAttribute();
            profileAttribute.setProfile(profile);

            // Iterate through the received attributes and store them

                profileAttribute.setAttribute(profileAttributeValueDTO.getAttribute());


                // Save the attribute to the database
                profileAttributeService.addOrUpdateAttribute(profile, attributeId, profileAttributeValueDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ProfileAttribute>> getAttributesByProfileId(@PathVariable Long profileId) {
        List<ProfileAttribute> attributes = profileAttributeService.getAttributes(profileId);
        return ResponseEntity.ok(attributes);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProfileAttribute>> getAttributesByProfileIdAndCategoryId(@PathVariable Long profileId, @PathVariable Long categoryId) {
            List<ProfileAttribute> attributes = profileAttributeService.getAttributeByProfileIdAndCategoryId(profileId, categoryId);
            return ResponseEntity.ok(attributes);
    }

//    @DeleteMapping("/{profileAttributeId}")
//    public ResponseEntity<Void> removeAttributes(@PathVariable Long profileAttributeId) {
//        profileAttributeService.deleteProfileAttribute(profileAttributeId);
//        return ResponseEntity.ok().build();
//    }
}
