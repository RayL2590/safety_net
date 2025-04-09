package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.AddressInfoDTO;
import com.ryan.safetynet.alerts.dto.FloodStationDTO;
import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import com.ryan.safetynet.alerts.utils.MedicalRecordUtils;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FloodAlertService {

    private final DataRepository dataRepository;
    private final FireStationService fireStationService;
    private final PersonService personService;

    @Autowired
    public FloodAlertService(DataRepository dataRepository, 
                             FireStationService fireStationService,
                             PersonService personService) {
        this.dataRepository = dataRepository;
        this.fireStationService = fireStationService;
        this.personService = personService;
    }

    /**
     * Récupère les foyers par stations de pompiers.
     *
     * @param stationNumbers Liste des numéros de stations
     * @return FloodStationDTO contenant les foyers groupés par adresse
     * @throws ResourceNotFoundException si une ou plusieurs stations n'existent pas
     */
    public FloodStationDTO getHouseholdsByStations(List<Integer> stationNumbers) {
        // Vérification de l'existence des stations
        List<String> nonExistentStations = stationNumbers.stream()
                .map(String::valueOf)
                .filter(station -> !fireStationService.existsByStationNumber(station))
                .collect(Collectors.toList());

        if (!nonExistentStations.isEmpty()) {
            String errorMessage = String.format("Les stations suivantes n'existent pas : %s", 
                String.join(", ", nonExistentStations));
            throw new ResourceNotFoundException(errorMessage);
        }

        // Adresses couvertes par les casernes sélectionnées en utilisant le service centralisé
        List<String> addressesCovered = fireStationService.getAddressesCoveredByStations(stationNumbers);

        // Utiliser le PersonService pour récupérer les personnes groupées par adresse
        Map<String, List<Person>> personsByAddress = personService.getPersonsByAddresses(addressesCovered);

        Data data = dataRepository.getData();
        List<AddressInfoDTO> addressInfos = new ArrayList<>();

        for (Map.Entry<String, List<Person>> entry : personsByAddress.entrySet()) {
            String address = entry.getKey();
            List<Person> residents = entry.getValue();

            List<PersonWithMedicalInfoDTO> residentInfos = residents.stream()
                    .map(person -> MedicalRecordUtils.extractMedicalInfo(person, data.getMedicalRecords()))
                    .collect(Collectors.toList());

            AddressInfoDTO addressInfo = new AddressInfoDTO();
            addressInfo.setAddress(address);
            addressInfo.setResidents(residentInfos);

            addressInfos.add(addressInfo);
        }

        FloodStationDTO response = new FloodStationDTO();
        response.setAddresses(addressInfos);

        return response;
    }
}
