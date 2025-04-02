package com.ryan.safetynet.alerts.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO représentant les informations de base d'une personne.
 * Utilisé pour transmettre les données essentielles d'une personne sans exposer toutes les informations du modèle.
 */
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class PersonDTO {
    @JsonProperty("firstName")
    private String firstName;
    
    @JsonProperty("lastName")
    private String lastName;
    
    @JsonProperty("address")
    private String address;
    
    @JsonProperty("phone")
    private String phone;
    
    @JsonProperty("age")
    private Integer age;
}
