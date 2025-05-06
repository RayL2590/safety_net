package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service gérant les alertes téléphoniques pour les casernes de pompiers.
 * Ce service permet de récupérer les numéros de téléphone des habitants
 * couverts par une caserne de pompiers spécifique.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneAlertService {

    private final FireStationService fireStationService;
    private final PersonService personService;

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
        log.debug("Recherche des numéros de téléphone pour la caserne {}", stationNumber);
        
        // Extraction des adresses couvertes par la caserne spécifiée
        List<String> addresses = fireStationService.getAddressesCoveredByStation(stationNumber);
        log.debug("Nombre d'adresses couvertes par la caserne {}: {}", stationNumber, addresses.size());

        // Utilisation de personService pour récupérer les personnes par adresses
        Map<String, List<Person>> personsByAddress = personService.getPersonsByAddresses(addresses);
        log.debug("Nombre d'adresses avec des résidents: {}", personsByAddress.size());

        // Extraction des numéros de téléphone uniques de toutes les personnes
        List<String> phoneNumbers = personsByAddress.values().stream()
                .flatMap(List::stream)
                .map(Person::getPhone)
                .distinct() // Élimination des doublons
                .collect(Collectors.toList());
        
        log.info("Nombre de numéros de téléphone uniques trouvés pour la caserne {}: {}", 
            stationNumber, phoneNumbers.size());
        return phoneNumbers;
    }
}

