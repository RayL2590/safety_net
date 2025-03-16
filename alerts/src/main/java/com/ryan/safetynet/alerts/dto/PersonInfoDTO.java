package com.ryan.safetynet.alerts.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * DTO représentant les informations détaillées d'une personne spécifique.
 * Utilisé pour l'endpoint /personInfo?firstName=X&lastName=Y qui retourne des informations
 * complètes sur une personne identifiée par son nom et prénom, incluant son adresse, son âge,
 * son email et ses informations médicales (médicaments et allergies). Ces données permettent
 * aux services d'urgence d'avoir une vue complète du profil médical d'un individu.
 */
@Getter
@Setter
public class PersonInfoDTO {
    private String firstName;
    private String lastName;
    private String address;
    private int age;
    private String email;
    private List<String> medications;
    private List<String> allergies;
}