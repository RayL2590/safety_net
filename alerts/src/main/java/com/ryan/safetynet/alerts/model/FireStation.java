package com.ryan.safetynet.alerts.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Modèle représentant une caserne de pompiers.
 * Cette classe établit une correspondance entre une adresse et un numéro de station.
 * Elle permet de déterminer quelle caserne de pompiers est responsable d'une zone géographique donnée.
 */
@Setter
@Getter
public class FireStation {

    /**
     * Adresse couverte par la caserne de pompiers.
     * Ce champ est obligatoire et ne peut pas être vide.
     * Il représente l'adresse complète d'un foyer ou d'un bâtiment.
     */
    @NotBlank(message = "L'adresse est obligatoire'")
    private String address;

    /**
     * Numéro de la caserne de pompiers.
     * Ce champ est obligatoire et ne peut pas être vide.
     * Il identifie de manière unique la caserne responsable de cette adresse.
     */
    @NotBlank(message = "La station est obligatoire")
    private String station;

    /**
     * Retourne une représentation textuelle de l'objet FireStation.
     * Cette méthode est utile pour le débogage et l'affichage des informations.
     *
     * @return une chaîne de caractères représentant l'objet FireStation
     */
    @Override
    public String toString() {
        return "FireStation{" +
                "address='" + address + '\'' +
                ", station='" + station + '\'' +
                '}';
    }

}
