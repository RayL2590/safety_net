package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.FireStation;
import com.ryan.safetynet.alerts.repository.DataRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FireStationService {

    private final DataRepository dataRepository;
    private final Validator validator;

    /**
     * Récupère les adresses couvertes par une liste de stations de pompiers.
     * Cette méthode centralise la logique d'extraction d'adresses pour éviter
     * la duplication de code dans les différents services.
     *
     * @param stationNumbers Liste des numéros de stations
     * @return Liste des adresses couvertes par ces stations
     */
    public List<String> getAddressesCoveredByStations(List<Integer> stationNumbers) {
        log.debug("Recherche des adresses couvertes par les stations: {}", stationNumbers);
        List<String> addresses = dataRepository.getData().getFireStations().stream()
                .filter(fs -> stationNumbers.contains(Integer.valueOf(fs.getStation())))
                .map(FireStation::getAddress)
                .collect(Collectors.toList());
        log.debug("Adresses trouvées: {}", addresses);
        return addresses;
    }

    /**
     * Récupère les adresses couvertes par une station de pompiers.
     * Surcharge pour faciliter l'utilisation avec un seul numéro de station.
     *
     * @param stationNumber Numéro de la station
     * @return Liste des adresses couvertes par cette station
     */
    public List<String> getAddressesCoveredByStation(Integer stationNumber) {
        log.debug("Recherche des adresses couvertes par la station: {}", stationNumber);
        List<String> addresses = dataRepository.getData().getFireStations().stream()
                .filter(fs -> fs.getStation().equals(String.valueOf(stationNumber)))
                .map(FireStation::getAddress)
                .collect(Collectors.toList());
        log.debug("Adresses trouvées: {}", addresses);
        return addresses;
    }

    /**
     * @param address L'adresse de la caserne
     * @return Un optional contenant la caserne trouvée ou vide si la caserne n'existe pas
     */
    public Optional<FireStation> findFireStationByAddress(String address) {
        log.debug("Recherche de la caserne à l'adresse: {}", address);
        Optional<FireStation> fireStation = dataRepository.getData().getFireStations().stream()
                .filter(f -> f.getAddress().equals(address))
                .findFirst();
        log.debug("Caserne trouvée: {}", fireStation.isPresent());
        return fireStation;
    }

    /**
     * @param fireStation La caserne à ajouter
     * @return la caserne ajoutée
     */
    public FireStation addFireStation(FireStation fireStation) throws IOException {
        log.info("Ajout d'une nouvelle caserne: {}", fireStation);
        var violations = validator.validate(fireStation);
        if (!violations.isEmpty()) {
            log.error("Erreur de validation lors de l'ajout de la caserne: {}", violations);
            throw new ConstraintViolationException("Erreur de validation dans FireStationService", violations);
        }
        List<FireStation> fireStations = dataRepository.getData().getFireStations();
        fireStations.add(fireStation);
        dataRepository.saveData();
        log.info("Caserne ajoutée avec succès");
        return fireStation;
    }

    /**
     * @return La caserne mise à jour, ou null si la caserne n'existe pas
     */
    public FireStation updateFireStation(FireStation fireStation) {
        log.info("Mise à jour de la caserne: {}", fireStation);
        var violations = validator.validate(fireStation);
        if (!violations.isEmpty()) {
            log.error("Erreur de validation lors de la mise à jour de la caserne: {}", violations);
            throw new ConstraintViolationException("Erreur de validation", violations);
        }

        Optional<FireStation> existing = findFireStationByAddress(fireStation.getAddress());
        if (existing.isPresent()) {
            FireStation f = existing.get();
            f.setStation(fireStation.getStation());
            try {
                dataRepository.saveData();
                log.info("Caserne mise à jour avec succès");
                return f;
            } catch (IOException e) {
                log.error("Erreur lors de la sauvegarde de la mise à jour de la caserne", e);
                throw new RuntimeException("Erreur lors de la sauvegarde", e);
            }
        }
        log.warn("Tentative de mise à jour d'une caserne inexistante à l'adresse: {}", fireStation.getAddress());
        return null;
    }

    /**
     * @param address L'adresse de la caserne à supprimer
     * @return true si la caserne a bien été supprimée, sinon false
     */
    public boolean deleteFireStationByAddress(String address) {
        log.info("Suppression de la caserne à l'adresse: {}", address);
        List<FireStation> fireStations = dataRepository.getData().getFireStations();
        boolean removed = fireStations.removeIf(f -> f.getAddress().equals(address));
        if (removed) {
            log.info("Caserne supprimée avec succès");
        } else {
            log.warn("Aucune caserne trouvée à l'adresse: {}", address);
        }
        return removed;
    }

    /**
     * @param station Le numéro de station à supprimer
     * @return le nombre de casernes supprimées
     */
    public int deleteFireStationsByStation(String station) {
        log.info("Suppression des casernes avec le numéro: {}", station);
        List<FireStation> fireStations = dataRepository.getData().getFireStations();
        int sizeBefore = fireStations.size();
        fireStations.removeIf(f -> f.getStation().equals(station));
        int removedCount = sizeBefore - fireStations.size();
        log.info("Nombre de casernes supprimées: {}", removedCount);
        return removedCount;
    }

    /**
     * Vérifie si un mapping existe déjà pour une adresse donnée.
     *
     * @param address L'adresse à vérifier
     * @return true si un mapping existe pour cette adresse, false sinon
     */
    public boolean existsByAddress(String address) {
        log.debug("Vérification de l'existence d'une caserne à l'adresse: {}", address);
        boolean exists = dataRepository.getData().getFireStations().stream()
                .anyMatch(fs -> fs.getAddress().equals(address));
        log.debug("Caserne trouvée: {}", exists);
        return exists;
    }

    /**
     * Vérifie si une station existe dans le système.
     *
     * @param stationNumber Le numéro de la station à vérifier
     * @return true si la station existe, false sinon
     */
    public boolean existsByStationNumber(String stationNumber) {
        log.debug("Vérification de l'existence de la station: {}", stationNumber);
        boolean exists = dataRepository.getData().getFireStations().stream()
                .anyMatch(fs -> fs.getStation().equals(stationNumber));
        log.debug("Station trouvée: {}", exists);
        return exists;
    }

    /**
     * Récupère la liste de toutes les casernes de pompiers enregistrées dans le système.
     *
     * @return Liste de toutes les casernes de pompiers
     */
    public List<FireStation> getAllFireStations() {
        log.debug("Récupération de toutes les casernes");
        List<FireStation> fireStations = dataRepository.getData().getFireStations();
        log.debug("Nombre de casernes trouvées: {}", fireStations.size());
        return fireStations;
    }
}
