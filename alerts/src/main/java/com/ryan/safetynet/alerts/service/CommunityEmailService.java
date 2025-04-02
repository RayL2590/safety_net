package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.CommunityEmailDTO;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service gérant les emails de la communauté.
 * Ce service permet de récupérer la liste des adresses email uniques
 * des habitants d'une ville spécifique. Cette fonctionnalité est utile
 * pour envoyer des communications de masse aux résidents d'une ville.
 */
@Service
public class CommunityEmailService {

    private final DataRepository dataRepository;

    /**
     * Constructeur du service avec injection de dépendance du repository.
     *
     * @param dataRepository le repository contenant les données de l'application
     */
    @Autowired
    public CommunityEmailService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * Récupère la liste des adresses email uniques des habitants d'une ville.
     * Cette méthode :
     * 1. Filtre les personnes par ville (insensible à la casse)
     * 2. Extrait leurs adresses email
     * 3. Élimine les doublons
     *
     * @param city la ville pour laquelle récupérer les emails
     * @return un DTO contenant la liste des adresses email uniques
     */
    public CommunityEmailDTO getEmailsByCity(String city) {
        // Récupération des données depuis le repository
        Data data = dataRepository.getData();

        // Filtrage des personnes par ville et extraction des emails uniques
        List<String> emails = data.getPersons().stream()
                .filter(p -> p.getCity().equalsIgnoreCase(city))
                .map(Person::getEmail)
                .distinct() // Élimination des doublons
                .collect(Collectors.toList());

        // Construction de la réponse
        CommunityEmailDTO dto = new CommunityEmailDTO();
        dto.setEmails(emails);

        return dto;
    }
}
