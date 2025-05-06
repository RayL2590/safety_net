package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.AddressInfoDTO;
import com.ryan.safetynet.alerts.dto.FloodStationDTO;
import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import com.ryan.safetynet.alerts.utils.MedicalRecordUtils;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FloodAlertService {

    private final DataRepository dataRepository;
    private final FireStationService fireStationService;
    private final PersonService personService;

    /**
     * Récupère les foyers par stations de pompiers.
     *
     * @param stationNumbers Liste des numéros de stations
     * @return FloodStationDTO contenant les foyers groupés par adresse
     * @throws ResourceNotFoundException si une ou plusieurs stations n'existent pas
     */
    public FloodStationDTO getHouseholdsByStations(List<Integer> stationNumbers) {
        log.info("Recherche des foyers pour les stations: {}", stationNumbers);

        // Vérification de l'existence des stations
        List<String> nonExistentStations = stationNumbers.stream()
                .map(String::valueOf)
                .filter(station -> !fireStationService.existsByStationNumber(station))
                .collect(Collectors.toList());

        if (!nonExistentStations.isEmpty()) {
            String errorMessage = String.format("Les stations suivantes n'existent pas : %s", 
                String.join(", ", nonExistentStations));
            log.error("Stations non trouvées: {}", nonExistentStations);
            throw new ResourceNotFoundException(errorMessage);
        }

        // Adresses couvertes par les casernes sélectionnées en utilisant le service centralisé
        List<String> addressesCovered = fireStationService.getAddressesCoveredByStations(stationNumbers);
        log.debug("Adresses couvertes par les stations: {}", addressesCovered);

        // Utiliser le PersonService pour récupérer les personnes groupées par adresse
        Map<String, List<Person>> personsByAddress = personService.getPersonsByAddresses(addressesCovered);
        log.debug("Nombre d'adresses avec des résidents: {}", personsByAddress.size());

        Data data = dataRepository.getData();
        List<AddressInfoDTO> addressInfos = new ArrayList<>();

        for (Map.Entry<String, List<Person>> entry : personsByAddress.entrySet()) {
            String address = entry.getKey();
            List<Person> residents = entry.getValue();
            log.debug("Traitement de l'adresse {} avec {} résidents", address, residents.size());

            List<PersonWithMedicalInfoDTO> residentInfos = residents.stream()
                    .map(person -> MedicalRecordUtils.extractMedicalInfo(person, data.getMedicalRecords()))
                    .collect(Collectors.toList());

            AddressInfoDTO addressInfo = new AddressInfoDTO();
            addressInfo.setAddress(address);
            addressInfo.setResidents(residentInfos);

            addressInfos.add(addressInfo);
            log.debug("Informations médicales extraites pour {} résidents à l'adresse {}", 
                residentInfos.size(), address);
        }

        FloodStationDTO response = new FloodStationDTO();
        response.setAddresses(addressInfos);

        log.info("Recherche terminée. {} adresses trouvées avec des résidents", addressInfos.size());
        return response;
    }
}
