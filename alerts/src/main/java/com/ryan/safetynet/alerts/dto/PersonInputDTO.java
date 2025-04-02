package com.ryan.safetynet.alerts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * DTO pour la saisie des informations d'une personne.
 * Ce DTO est utilisé pour valider et transférer les données lors de la création
 * ou de la mise à jour d'une personne dans le système.
 * Il inclut des validations pour s'assurer que toutes les données sont correctement formatées.
 */
@Data
public class PersonInputDTO {
    /** Prénom de la personne */
    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    /** Nom de famille de la personne */
    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    /** Adresse complète de la personne */
    @NotBlank(message = "L'adresse est obligatoire")
    private String address;

    /** Ville de résidence */
    @NotBlank(message = "La ville est obligatoire")
    private String city;

    /** Code postal au format 5 chiffres */
    @NotBlank(message = "Le code postal est obligatoire")
    @Pattern(regexp = "^[0-9]{5}$", message = "Le code postal doit contenir 5 chiffres")
    private String zip;

    /** Numéro de téléphone au format XXX-XXX-XXXX */
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$", message = "Le numéro de téléphone doit être au format XXX-XXX-XXXX")
    private String phone;

    /** Adresse email valide */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;
} 