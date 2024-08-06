package com.xontext.pmp.dto;

import com.xontext.pmp.model.Attribute;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ProfileAttributeValueDTO {
    private Long profileId;
    private Long attributeId;
    private String attributeName;
    private String valueType;
    private String stringValue;
    private Integer intValue;
    private Float floatValue;
    private Boolean boolValue;
    private Date dateValue;
    private Attribute attribute;

    public Object getValue() {
        switch (valueType) {
            case "STRING":
                return stringValue;
            case "INTEGER":
                return intValue;
            case "FLOAT":
                return floatValue;
            case "BOOLEAN":
                return boolValue;
            case "DATE":
                return dateValue;
            default:
                throw new IllegalArgumentException("Invalid value type");
        }
    }
}
