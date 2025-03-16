package com.ryan.safetynet.alerts.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * DTO représentant les informations d'alerte incendie pour une adresse donnée.
 * Utilisé pour l'endpoint /fire?address=X qui retourne la liste des habitants
 * à cette adresse avec leurs informations médicales et le numéro de la caserne desservant l'adresse.
 */
@Getter
@Setter
public class FireAlertDTO {
    private List<PersonWithMedicalInfoDTO> residents;
    private String fireStationNumber;
}