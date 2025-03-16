package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.model.FireStation;
import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final DataRepository dataRepository;

    @Autowired
    public AlertService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * Récupère les personnes couvertes par une station de pompiers.
     *
     * @param stationNumber Le numéro de la station de pompiers.
     * @return Une map contenant la liste des personnes et le décompte des adultes et enfants.
     */
    public Map<String, Object> getPersonsCoveredByStation(int stationNumber) {
        // Récupérer les données une seule fois
        Data data = dataRepository.getData();
        List<Person> persons = data.getPersons();
        List<FireStation> fireStations = data.getFireStations();
        List<MedicalRecord> medicalRecords = data.getMedicalRecords();

        // Trouver les adresses couvertes par la station
        List<String> addresses = fireStations.stream()
                .filter(fs -> fs.getStation().equals(String.valueOf(stationNumber)))
                .map(FireStation::getAddress)
                .collect(Collectors.toList());

        // Filtrer les personnes vivant à ces adresses
        List<Person> coveredPersons = persons.stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .collect(Collectors.toList());

        // Calculer le nombre d'adultes et d'enfants
        long childCount = coveredPersons.stream()
                .filter(p -> calculateAge(getBirthdate(p.getFirstName(), p.getLastName(), medicalRecords)) <= 18)
                .count();
        long adultCount = coveredPersons.size() - childCount;

        // Préparer la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("persons", coveredPersons);
        response.put("adultCount", adultCount);
        response.put("childCount", childCount);

        return response;
    }

    /**
     * Récupère les enfants vivant à une adresse donnée.
     *
     * @param address L'adresse à vérifier.
     * @return Une map contenant la liste des enfants et les autres membres du foyer.
     */
    public Map<String, Object> getChildrenAtAddress(String address) {
        // Récupérer les données une seule fois
        Data data = dataRepository.getData();
        List<Person> persons = data.getPersons();
        List<MedicalRecord> medicalRecords = data.getMedicalRecords();

        // Trouver les enfants à l'adresse donnée
        List<Person> children = persons.stream()
                .filter(p -> p.getAddress().equals(address) && calculateAge(getBirthdate(p.getFirstName(), p.getLastName(), medicalRecords)) <= 18)
                .collect(Collectors.toList());

        // Trouver les autres membres du foyer
        List<Person> householdMembers = persons.stream()
                .filter(p -> p.getAddress().equals(address) && calculateAge(getBirthdate(p.getFirstName(), p.getLastName(), medicalRecords)) > 18)
                .collect(Collectors.toList());

        // Préparer la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("children", children);
        response.put("householdMembers", householdMembers);

        return response;
    }

    /**
     * Récupère les numéros de téléphone des personnes couvertes par une station de pompiers.
     *
     * @param stationNumber Le numéro de la station de pompiers.
     * @return Une liste de numéros de téléphone.
     */
    public List<String> getPhoneNumbersByStation(int stationNumber) {
        // Récupérer les données une seule fois
        Data data = dataRepository.getData();
        List<Person> persons = data.getPersons();
        List<FireStation> fireStations = data.getFireStations();

        // Trouver les adresses couvertes par la station
        List<String> addresses = fireStations.stream()
                .filter(fs -> fs.getStation().equals(String.valueOf(stationNumber)))
                .map(FireStation::getAddress)
                .collect(Collectors.toList());

        // Récupérer les numéros de téléphone des personnes vivant à ces adresses
        return persons.stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .map(Person::getPhone)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Récupère les informations des habitants d'une adresse donnée, ainsi que la station de pompiers qui les dessert.
     *
     * @param address L'adresse à vérifier.
     * @return Une map contenant la liste des habitants et le numéro de la station de pompiers.
     */
    public Map<String, Object> getPersonsAndFireStationByAddress(String address) {
        // Récupérer les données une seule fois
        Data data = dataRepository.getData();
        List<Person> persons = data.getPersons();
        List<FireStation> fireStations = data.getFireStations();

        // Trouver les habitants de l'adresse donnée
        List<Person> residents = persons.stream()
                .filter(p -> p.getAddress().equals(address))
                .collect(Collectors.toList());

        // Trouver la station de pompiers desservant cette adresse
        Optional<FireStation> fireStation = fireStations.stream()
                .filter(fs -> fs.getAddress().equals(address))
                .findFirst();

        // Préparer la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("residents", residents);
        response.put("fireStationNumber", fireStation.map(FireStation::getStation).orElse(null));

        return response;
    }

    /**
     * Calcule l'âge d'une personne à partir de sa date de naissance.
     *
     * @param birthdate La date de naissance.
     * @return L'âge de la personne.
     */
    private int calculateAge(LocalDate birthdate) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthdate, currentDate).getYears();
    }

    /**
     * Récupère la date de naissance d'une personne à partir de son prénom et de son nom.
     *
     * @param firstName      Le prénom de la personne.
     * @param lastName       Le nom de famille de la personne.
     * @param medicalRecords La liste des dossiers médicaux.
     * @return La date de naissance.
     */
    private LocalDate getBirthdate(String firstName, String lastName, List<MedicalRecord> medicalRecords) {
        return medicalRecords.stream()
                .filter(mr -> mr.getFirstName().equals(firstName) && mr.getLastName().equals(lastName))
                .map(MedicalRecord::getBirthdate)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Medical record not found"));
    }
}