package com.ryan.safetynet.alerts.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe de modèle représentant l'ensemble des données de l'application SafetyNet Alerts.
 * Cette classe sert de conteneur principal pour toutes les entités du système.
 * Elle est utilisée notamment pour la désérialisation des données JSON lors du chargement initial.
 */
@Setter
@Getter
public class Data {
    /**
     * Liste des personnes enregistrées dans le système.
     * Chaque personne contient des informations personnelles comme le nom, l'adresse, etc.
     * Cette propriété est mappée au champ "persons" dans le fichier JSON.
     */
    @JsonProperty("persons")
    private List<Person> persons = new ArrayList<>();

    /**
     * Liste des casernes de pompiers avec leurs adresses associées.
     * Chaque caserne est identifiée par un numéro et couvre une ou plusieurs adresses.
     * Cette propriété est mappée au champ "firestations" dans le fichier JSON.
     */
    @JsonProperty("firestations")
    private List<FireStation> fireStations = new ArrayList<>();

    /**
     * Liste des dossiers médicaux des personnes.
     * Chaque dossier contient des informations médicales comme la date de naissance,
     * les médicaments et les allergies d'une personne.
     * Cette propriété est mappée au champ "medicalrecords" dans le fichier JSON.
     */
    @JsonProperty("medicalrecords")
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

}
