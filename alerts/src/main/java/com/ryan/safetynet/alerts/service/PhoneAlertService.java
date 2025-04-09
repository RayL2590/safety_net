package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service gérant les alertes téléphoniques pour les casernes de pompiers.
 * Ce service permet de récupérer les numéros de téléphone des habitants
 * couverts par une caserne de pompiers spécifique.
 */
@Service
public class PhoneAlertService {

    private final DataRepository dataRepository;
    private final FireStationService fireStationService;
    private final PersonService personService;

    /**
     * Constructeur du service avec injection de dépendance du repository.
     *
     * @param dataRepository le repository contenant les données de l'application
     * @param fireStationService le service gérant les casernes de pompiers
     * @param personService le service gérant les personnes
     */
    @Autowired
    public PhoneAlertService(DataRepository dataRepository, 
                            FireStationService fireStationService,
                            PersonService personService) {
        this.dataRepository = dataRepository;
        this.fireStationService = fireStationService;
        this.personService = personService;
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
        // Extraction des adresses couvertes par la caserne spécifiée
        List<String> addresses = fireStationService.getAddressesCoveredByStation(stationNumber);

        // Utilisation de personService pour récupérer les personnes par adresses
        Map<String, List<Person>> personsByAddress = personService.getPersonsByAddresses(addresses);

        // Extraction des numéros de téléphone uniques de toutes les personnes
        return personsByAddress.values().stream()
                .flatMap(List::stream)
                .map(Person::getPhone)
                .distinct() // Élimination des doublons
                .collect(Collectors.toList());
    }
}

