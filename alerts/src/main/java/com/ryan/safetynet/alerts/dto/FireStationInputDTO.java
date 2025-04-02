package com.ryan.safetynet.alerts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO pour la saisie des informations d'une caserne de pompiers.
 * Ce DTO est utilisé pour valider et transférer les données lors de la création
 * ou de la mise à jour d'une caserne de pompiers dans le système.
 * Il inclut les informations essentielles comme le numéro de la caserne et son adresse.
 */
@Data
public class FireStationInputDTO {
    /** Numéro unique de la caserne de pompiers */
    @NotNull(message = "Le numéro de la caserne est obligatoire")
    @Positive(message = "Le numéro de la caserne doit être positif")
    private Integer station;

    /** Adresse complète de la caserne de pompiers */
    @NotBlank(message = "L'adresse est obligatoire")
    private String address;
} 