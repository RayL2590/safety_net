package com.ryan.safetynet.alerts.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO représentant les informations de base d'une personne.
 * Utilisé pour transmettre les données essentielles d'une personne sans exposer toutes les informations du modèle.
 */
@Getter
@Setter
@ToString
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
    
    /**
     * Constructeur par défaut
     */
    public PersonDTO() {
    }
    
    /**
     * Constructeur avec les informations de base d'une personne
     * 
     * @param firstName Le prénom de la personne
     * @param lastName Le nom de famille de la personne
     * @param address L'adresse de la personne
     * @param phone Le numéro de téléphone de la personne
     */
    public PersonDTO(String firstName, String lastName, String address, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
    }
    
    /**
     * Constructeur avec les informations de base d'une personne incluant l'âge
     * 
     * @param firstName Le prénom de la personne
     * @param lastName Le nom de famille de la personne
     * @param address L'adresse de la personne
     * @param phone Le numéro de téléphone de la personne
     * @param age L'âge de la personne
     */
    public PersonDTO(String firstName, String lastName, String address, String phone, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.age = age;
    }
    
    // Ajout explicite des getters/setters pour s'assurer qu'ils sont correctement générés

}
