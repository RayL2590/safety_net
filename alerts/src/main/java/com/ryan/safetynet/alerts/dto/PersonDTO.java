package com.ryan.safetynet.alerts.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO représentant les informations de base d'une personne.
 * Utilisé pour transmettre les données essentielles d'une personne sans exposer toutes les informations du modèle.
 */
@Getter
@Setter
public class PersonDTO {
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
}
