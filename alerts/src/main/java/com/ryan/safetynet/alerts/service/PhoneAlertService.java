package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.FireStation;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service gérant les alertes téléphoniques pour les casernes de pompiers.
 * Ce service permet de récupérer les numéros de téléphone des habitants
 * couverts par une caserne de pompiers spécifique.
 */
@Service
public class PhoneAlertService {

    private final DataRepository dataRepository;

    /**
     * Constructeur du service avec injection de dépendance du repository.
     *
     * @param dataRepository le repository contenant les données de l'application
     */
    @Autowired
    public PhoneAlertService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * Récupère la liste des numéros de téléphone des habitants couverts par une caserne.
     * Cette méthode :
     * 1. Récupère toutes les adresses couvertes par la caserne
     * 2. Filtre les personnes vivant à ces adresses
     * 3. Extrait leurs numéros de téléphone uniques
     *
     * @param stationNumber le numéro de la caserne de pompiers
     * @return la liste des numéros de téléphone uniques des habitants couverts par la caserne
     */
    public List<String> getPhoneNumbersByStation(int stationNumber) {
        // Récupération des données depuis le repository
        Data data = dataRepository.getData();
        List<Person> persons = data.getPersons();
        List<FireStation> fireStations = data.getFireStations();

        // Extraction des adresses couvertes par la caserne spécifiée
        List<String> addresses = fireStations.stream()
                .filter(fs -> fs.getStation().equals(String.valueOf(stationNumber)))
                .map(FireStation::getAddress)
                .toList();

        // Filtrage des personnes par adresse et extraction des numéros de téléphone uniques
        return persons.stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .map(Person::getPhone)
                .distinct() // Élimination des doublons
                .collect(Collectors.toList());
    }
}

