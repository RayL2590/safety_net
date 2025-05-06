package com.ryan.safetynet.alerts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(min = 1, max = 4, message = "Le numéro de la caserne doit contenir entre 1 et 4 chiffres")
    @Pattern(regexp = "^[0-9]+$", message = "Le numéro de la caserne doit contenir uniquement des chiffres")
    private String fireStationNumber;
}