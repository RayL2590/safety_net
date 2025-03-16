package com.ryan.safetynet.alerts.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO représentant les informations d'un enfant (personne de 18 ans ou moins).
 * Inclut l'âge calculé à partir de la date de naissance.
 */
@Getter
@Setter
public class ChildDTO {
    private String firstName;
    private String lastName;
    private int age;
}