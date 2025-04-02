package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.FireStation;
import com.ryan.safetynet.alerts.repository.DataRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FireStationService {

    private final DataRepository dataRepository;
    private final Validator validator;

    @Autowired
    public FireStationService(DataRepository dataRepository, Validator validator) {
        this.dataRepository = dataRepository;
        this.validator = validator;
    }

    /**
     * @return la liste de toutes les casernes de pompiers
     */
    public List<FireStation> getAllFireStations() {
        return dataRepository.getData().getFireStations();
    }

    /**
     * @param address L'adresse de la caserne
     * @return Un optional contenant la caserne trouvée ou vide si la caserne n'existe pas
     */
    public Optional<FireStation> findFireStationByAddress(String address) {
        return dataRepository.getData().getFireStations().stream()
                .filter(f -> f.getAddress().equals(address))
                .findFirst();
    }

    /**
     * @param station Le numéro de la caserne
     * @return La liste des casernes correspondant au numéro de station
     */
    public List<FireStation> findFireStationsByStation(String station) {
        return dataRepository.getData().getFireStations().stream()
                .filter(f -> f.getStation().equals(station))
                .collect(Collectors.toList());
    }

    /**
     * @param fireStation La caserne à ajouter
     * @return la caserne ajoutée
     */
    public FireStation addFireStation(FireStation fireStation) throws IOException {
        var violations = validator.validate(fireStation);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Erreur de validation dans FireStationService", violations);
        }
        List<FireStation> fireStations = dataRepository.getData().getFireStations();
        fireStations.add(fireStation);
        dataRepository.saveData();
        return fireStation;
    }

    /**
     * @return La caserne mise à jour, ou null si la caserne n'existe pas
     */
    public FireStation updateFireStation(FireStation fireStation) {
        var violations = validator.validate(fireStation);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException("Erreur de validation", violations);
        }

        Optional<FireStation> existing = findFireStationByAddress(fireStation.getAddress());
        if (existing.isPresent()) {
            FireStation f = existing.get();
            f.setStation(fireStation.getStation());
            try {
                dataRepository.saveData();
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la sauvegarde", e);
            }
            return f;
        }
        return null;
    }


    /**
     * @param address L'adresse de la caserne à supprimer
     * @return true si la caserne a bien été supprimée, sinon false
     */
    public boolean deleteFireStationByAddress(String address) {
        List<FireStation> fireStations = dataRepository.getData().getFireStations();
        return fireStations.removeIf(f -> f.getAddress().equals(address));
    }

    /**
     * @param station Le numéro de station à supprimer
     * @return le nombre de casernes supprimées
     */
    public int deleteFireStationsByStation(String station) {
        List<FireStation> fireStations = dataRepository.getData().getFireStations();
        int sizeBefore = fireStations.size();
        fireStations.removeIf(f -> f.getStation().equals(station));
        return sizeBefore - fireStations.size();
    }

    /**
     * Vérifie si un mapping existe déjà pour une adresse donnée.
     *
     * @param address L'adresse à vérifier
     * @return true si un mapping existe pour cette adresse, false sinon
     */
    public boolean existsByAddress(String address) {
        return dataRepository.getData().getFireStations().stream()
                .anyMatch(fs -> fs.getAddress().equals(address));
    }

    /**
     * Vérifie si une station existe dans le système.
     *
     * @param stationNumber Le numéro de la station à vérifier
     * @return true si la station existe, false sinon
     */
    public boolean existsByStationNumber(String stationNumber) {
        return dataRepository.getData().getFireStations().stream()
                .anyMatch(fs -> fs.getStation().equals(stationNumber));
    }

    /**
     * Récupère l'ensemble des numéros de stations existants.
     *
     * @return Set des numéros de stations existants
     */
    public Set<String> getAllStationNumbers() {
        return dataRepository.getData().getFireStations().stream()
                .map(FireStation::getStation)
                .collect(Collectors.toSet());
    }
}
