package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.FireAlertDTO;
import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
import com.ryan.safetynet.alerts.model.*;
import com.ryan.safetynet.alerts.repository.DataRepository;
import com.ryan.safetynet.alerts.utils.MedicalRecordUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Service
public class FireAlertService {

    private final DataRepository dataRepository;

    /**
     * Constructeur du service avec injection de dépendance du repository.
     *
     * @param dataRepository le repository contenant les données de l'application
     */
    @Autowired
    public FireAlertService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

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
     * @throws IllegalStateException si un dossier médical est manquant pour un résident
     */
    public FireAlertDTO getPersonsAndFireStationByAddress(String address) {
        // Récupération des données depuis le repository
        Data data = dataRepository.getData();
        List<Person> persons = data.getPersons();
        List<FireStation> fireStations = data.getFireStations();
        List<MedicalRecord> medicalRecords = data.getMedicalRecords();

        // Filtrage et transformation des résidents avec leurs informations médicales
        List<PersonWithMedicalInfoDTO> residents = persons.stream()
                .filter(p -> p.getAddress().equals(address))
                .map(p -> MedicalRecordUtils.extractMedicalInfo(p, medicalRecords))
                .collect(Collectors.toList());

        // Recherche de la caserne de pompiers responsable de l'adresse
        Optional<FireStation> fireStation = fireStations.stream()
                .filter(fs -> fs.getAddress().equals(address))
                .findFirst();

        // Extraction du numéro de caserne ou "Inconnu" si non trouvé
        String stationNumber = fireStation
                .map(FireStation::getStation)
                .orElse("Inconnu");

        // Construction de la réponse
        FireAlertDTO response = new FireAlertDTO();
        response.setResidents(residents);
        response.setFireStationNumber(stationNumber);

        return response;
    }
}
