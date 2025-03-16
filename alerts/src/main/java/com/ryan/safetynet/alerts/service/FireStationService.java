package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.model.FireStation;
import com.ryan.safetynet.alerts.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FireStationService {
    private final DataRepository dataRepository;

    @Autowired
    public FireStationService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
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
    public FireStation addFireStation(FireStation fireStation) {
        List<FireStation> fireStations = dataRepository.getData().getFireStations();
        fireStations.add(fireStation);
        return fireStation;
    }

    /**
     * @param address L'adresse de la caserne à mettre à jour
     * @param station Le nouveau numéro de station
     * @return La caserne mise à jour, ou null si la caserne n'existe pas
     */
    public FireStation updateFireStation(String address, String station) {
        Optional<FireStation> existingFireStation = findFireStationByAddress(address);
        if (existingFireStation.isPresent()) {
            FireStation f = existingFireStation.get();
            f.setStation(station);
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
}
