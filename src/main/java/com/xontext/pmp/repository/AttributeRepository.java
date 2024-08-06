package com.xontext.pmp.repository;

import com.xontext.pmp.model.Attribute;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    List<Attribute> findByCategoryId(Long categoryId);
    List<Attribute> findByCategoryName(String categoryName);
    @Transactional
    void deleteByCategoryId(Long id);
}
