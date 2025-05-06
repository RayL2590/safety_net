package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.FireAlertDTO;
import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
import com.ryan.safetynet.alerts.model.*;
import com.ryan.safetynet.alerts.repository.DataRepository;
import com.ryan.safetynet.alerts.utils.MedicalRecordUtils;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service gérant les alertes incendie.
 * Ce service permet de récupérer les informations des habitants d'une adresse
 * en cas d'incendie, incluant leurs informations médicales et le numéro de la
 * caserne de pompiers responsable.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FireAlertService {

    private final DataRepository dataRepository;
    private final FireStationService fireStationService;

    /**
     * Récupère les informations des habitants d'une adresse en cas d'incendie.
     * Cette méthode :
     * 1. Filtre les résidents à l'adresse spécifiée
     * 2. Récupère leurs informations médicales
     * 3. Calcule leur âge
     * 4. Identifie la caserne de pompiers responsable
     *
     * @param address l'adresse à vérifier
     * @return un DTO contenant la liste des résidents avec leurs informations médicales
     *         et le numéro de la caserne de pompiers
     * @throws ResourceNotFoundException si aucune station n'est associée à l'adresse
     *         ou si la station n'existe pas dans le système
     */
    public FireAlertDTO getPersonsAndFireStationByAddress(String address) {
        log.info("Recherche des informations pour l'adresse : {}", address);

        try {
            // Récupération des données depuis le repository
            Data data = dataRepository.getData();
            List<Person> persons = data.getPersons();
            List<FireStation> fireStations = data.getFireStations();
            List<MedicalRecord> medicalRecords = data.getMedicalRecords();

            log.debug("Nombre total de personnes : {}, stations : {}, dossiers médicaux : {}", 
                    persons.size(), fireStations.size(), medicalRecords.size());

            // Filtrage et transformation des résidents avec leurs informations médicales
            List<PersonWithMedicalInfoDTO> residents = persons.stream()
                    .filter(p -> p.getAddress().equals(address))
                    .map(p -> MedicalRecordUtils.extractMedicalInfo(p, medicalRecords))
                    .collect(Collectors.toList());

            log.debug("Nombre de résidents trouvés à l'adresse {} : {}", address, residents.size());

            // Recherche de la caserne de pompiers responsable de l'adresse
            Optional<FireStation> fireStation = fireStations.stream()
                    .filter(fs -> fs.getAddress().equals(address))
                    .findFirst();

            // Vérification de l'existence de la station
            if (fireStation.isEmpty()) {
                log.error("Aucune station de pompiers n'est associée à l'adresse : {}", address);
                throw new ResourceNotFoundException("Aucune station de pompiers n'est associée à l'adresse : " + address);
            }

            // Extraction du numéro de caserne
            String stationNumber = fireStation.get().getStation();
            log.debug("Station trouvée pour l'adresse {} : {}", address, stationNumber);

            // Vérification que la station existe dans le système
            if (!fireStationService.existsByStationNumber(stationNumber)) {
                log.error("La station de pompiers {} n'existe pas dans le système", stationNumber);
                throw new ResourceNotFoundException("La station de pompiers " + stationNumber + " n'existe pas dans le système");
            }

            // Construction de la réponse
            FireAlertDTO response = new FireAlertDTO();
            response.setResidents(residents);
            response.setFireStationNumber(stationNumber);

            log.info("Informations récupérées avec succès pour l'adresse : {}", address);
            return response;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des informations pour l'adresse {} : {}", address, e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des informations : " + e.getMessage(), e);
        }
    }
}
