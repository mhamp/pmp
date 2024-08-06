package com.xontext.pmp.model;

import lombok.Data;

@Data
public class Address {
    private String street;           // Street address
    private String city;             // City
    private String state;            // State/Province/Region
    private String postalCode;       // Postal/ZIP code
    private String country;          // Country
    private String additionalInfo;
}
