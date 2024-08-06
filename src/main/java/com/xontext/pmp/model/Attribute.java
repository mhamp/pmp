package com.xontext.pmp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private boolean isRequired;

    @Enumerated(EnumType.STRING)
    private ValueType valueType;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public enum ValueType {
        STRING, INTEGER, FLOAT, BOOLEAN, DATE
    }
}
