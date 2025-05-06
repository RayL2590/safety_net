package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.CommunityEmailDTO;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service gérant les emails de la communauté.
 * Ce service permet de récupérer la liste des adresses email uniques
 * des habitants d'une ville spécifique. Cette fonctionnalité est utile
 * pour envoyer des communications de masse aux résidents d'une ville.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CommunityEmailService {

    private final DataRepository dataRepository;

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
        log.info("Recherche des emails pour la ville : {}", city);

        try {
            // Récupération des données depuis le repository
            Data data = dataRepository.getData();
            log.debug("Nombre total de personnes dans le système : {}", data.getPersons().size());

            // Filtrage des personnes par ville et extraction des emails uniques
            List<String> emails = data.getPersons().stream()
                    .filter(p -> p.getCity().equalsIgnoreCase(city))
                    .map(Person::getEmail)
                    .distinct()
                    .collect(Collectors.toList());

            log.debug("Nombre d'emails uniques trouvés pour {} : {}", city, emails.size());

            // Construction de la réponse
            CommunityEmailDTO dto = new CommunityEmailDTO();
            dto.setEmails(emails);

            log.info("Emails récupérés avec succès pour la ville : {}", city);
            return dto;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des emails pour la ville {} : {}", city, e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération des emails : " + e.getMessage(), e);
        }
    }
}
