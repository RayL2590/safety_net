package com.ryan.safetynet.alerts.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * DTO représentant une personne avec ses informations médicales.
 * Utilisé pour les endpoints nécessitant des informations médicales détaillées
 * comme /fire, /flood/stations ou /personInfo.
 */
@Getter
@Setter
public class PersonWithMedicalInfoDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private int age;
    private List<String> medications;
    private List<String> allergies;
}