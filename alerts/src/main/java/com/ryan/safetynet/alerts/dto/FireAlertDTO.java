package com.ryan.safetynet.alerts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "La liste des résidents ne peut pas être null")
    private List<PersonWithMedicalInfoDTO> residents;

    @NotBlank(message = "Le numéro de la caserne est obligatoire")
    private String fireStationNumber;
}