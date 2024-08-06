package com.xontext.pmp.service.core;

import com.xontext.pmp.model.Attribute;
import com.xontext.pmp.repository.AttributeRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AttributeService {

    private final AttributeRepository attributeRepository;
    public Attribute createAttribute(Attribute attribute) {
        return attributeRepository.save(attribute);
    }

    public List<Attribute> getAllAttributes() {
        return attributeRepository.findAll();
    }

    public List<Attribute> getAllAttributesByCategoryName(String categoryName) {
        return attributeRepository.findByCategoryName(categoryName);
    }

    public List<Attribute> getAllAttributesByCategoryId(Long id) {
        return attributeRepository.findByCategoryId(id);
    }

    public Attribute getAttributeById(Long attributeId) {
        return attributeRepository.findById(attributeId)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));
    }

    public Attribute updateAttribute(Long attributeId, Attribute attribute) {
        Attribute existingAttribute = attributeRepository.findById(attributeId).orElseThrow(() -> new RuntimeException("Attribute not found"));
        existingAttribute.setName(attribute.getName());
        existingAttribute.setDescription(attribute.getDescription());
        existingAttribute.setRequired(attribute.isRequired());
        return attributeRepository.save(existingAttribute);
    }

    public void deleteAttribute(Long attributeId) {
        attributeRepository.deleteById(attributeId);
    }
    @Transactional
    public void deleteAttributesByCategoryId(Long id) {
        attributeRepository.deleteByCategoryId(id);
    }
}
