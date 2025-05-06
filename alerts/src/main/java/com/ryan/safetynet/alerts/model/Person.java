package com.ryan.safetynet.alerts.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Modèle représentant une personne dans le système.
 * Cette classe contient les informations de base d'une personne,
 * notamment son nom, son adresse et ses coordonnées.
 * Ces informations sont essentielles pour les services d'urgence
 * pour localiser et contacter les personnes en cas de besoin.
 */
@Setter
@Getter
public class Person {
    /**
     * Prénom de la personne.
     * Ce champ est obligatoire et ne peut pas être vide.
     */
    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;

    /**
     * Nom de la personne.
     * Ce champ est obligatoire et ne peut pas être vide.
     */
    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    /**
     * Adresse complète de la personne.
     * Ce champ est obligatoire et ne peut pas être vide.
     * Il est utilisé pour déterminer quelle caserne de pompiers couvre cette adresse.
     */
    @NotBlank(message = "L'adresse est obligatoire")
    private String address;

    /**
     * Ville de résidence de la personne.
     * Ce champ est obligatoire et ne peut pas être vide.
     */
    @NotBlank(message = "La ville est obligatoire")
    private String city;

    /**
     * Code postal de la personne.
     * Ce champ est obligatoire et doit contenir exactement 5 chiffres.
     */
    @NotBlank(message = "Le code postal est obligatoire")
    @Pattern(regexp = "\\d{5}", message = "Le code postal doit contenir 5 chiffres")
    private String zip;

    /**
     * Numéro de téléphone de la personne.
     * Ce champ est obligatoire et doit respecter le format XXX-XXX-XXXX.
     */
    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^\\d{3}-\\d{3}-\\d{4}$", message = "Format de téléphone invalide (ex: 123-456-7890)")
    private String phone;

    /**
     * Adresse email de la personne.
     * Ce champ est obligatoire et doit être une adresse email valide.
     */
    @NotBlank(message = "L'email est obligatoire")
    @jakarta.validation.constraints.Email(message = "Email invalide")
    private String email;
    
    /**
     * Constructeur par défaut.
     * Nécessaire pour la désérialisation JSON et la création d'instances via des frameworks.
     */
    public Person() {
    }
    
    /**
     * Constructeur avec tous les paramètres.
     * Permet de créer une instance de Person avec toutes ses propriétés.
     *
     * @param firstName prénom de la personne
     * @param lastName nom de la personne
     * @param address adresse de la personne
     * @param city ville de résidence
     * @param zip code postal
     * @param phone numéro de téléphone
     * @param email adresse email
     */
    public Person(String firstName, String lastName, String address, String city, String zip, String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.zip = zip;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Retourne une représentation textuelle de l'objet Person.
     * Cette méthode est utile pour le débogage et l'affichage des informations.
     *
     * @return une chaîne de caractères représentant l'objet Person
     */
    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", zip='" + zip + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
