package com.xontext.pmp.controller;

import com.xontext.pmp.model.Attribute;
import com.xontext.pmp.service.core.AttributeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attributes")
@AllArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;

    @PostMapping
    public ResponseEntity createAttribute(@RequestBody Attribute attribute) {
        Attribute savedAttribute = attributeService.createAttribute(attribute);
        return ResponseEntity.ok(savedAttribute);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Attribute> getAttribute(@PathVariable Long id) {
        Attribute attribute = attributeService.getAttributeById(id);
        return ResponseEntity.ok(attribute);
    }
    @GetMapping
    public ResponseEntity<List<Attribute>> getAttributes() {
        List<Attribute> attributes = attributeService.getAllAttributes();
        return ResponseEntity.ok(attributes);
    }
    @GetMapping("/category/{id}")
    public ResponseEntity<List<Attribute>> getAllAttributesByCategoryId(@PathVariable Long id) {
        List<Attribute> attributes = attributeService.getAllAttributesByCategoryId(id);
        return ResponseEntity.ok(attributes);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Attribute> updateAttribute(@PathVariable Long id, @RequestBody Attribute attribute) {
        Attribute updatedAttribute = attributeService.updateAttribute(id, attribute);
        return ResponseEntity.ok(updatedAttribute);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity deleteAttribute(@PathVariable Long id) {
        attributeService.deleteAttribute(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/category/{id}")
    public ResponseEntity deleteAttributesByCategoryId(@PathVariable Long id){
        attributeService.deleteAttributesByCategoryId(id);
        return ResponseEntity.noContent().build();
    }
}
