package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.AddressInfoDTO;
import com.ryan.safetynet.alerts.dto.FloodStationDTO;
import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.FireStation;
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

    @Autowired
    public FloodAlertService(DataRepository dataRepository, FireStationService fireStationService) {
        this.dataRepository = dataRepository;
        this.fireStationService = fireStationService;
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

        Data data = dataRepository.getData();

        // Adresses couvertes par les casernes sélectionnées
        Set<String> addressesCovered = data.getFireStations().stream()
                .filter(fs -> stationNumbers.contains(Integer.valueOf(fs.getStation())))
                .map(FireStation::getAddress)
                .collect(Collectors.toSet());

        // Grouper les personnes par adresse
        Map<String, List<Person>> personsByAddress = data.getPersons().stream()
                .filter(p -> addressesCovered.contains(p.getAddress()))
                .collect(Collectors.groupingBy(Person::getAddress));

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
