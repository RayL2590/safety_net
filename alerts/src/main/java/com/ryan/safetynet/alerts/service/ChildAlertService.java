package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.ChildAlertDTO;
import com.ryan.safetynet.alerts.dto.ChildDTO;
import com.ryan.safetynet.alerts.dto.HouseholdMemberDTO;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ryan.safetynet.alerts.utils.AgeCalculator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service gérant les alertes concernant les enfants.
 * Ce service permet de récupérer les informations sur les enfants (≤ 18 ans)
 * vivant à une adresse donnée, ainsi que les informations sur les autres
 * membres du foyer. Cette fonctionnalité est utile pour les services d'urgence
 * qui doivent identifier rapidement la présence d'enfants dans un foyer.
 */
@Service
public class ChildAlertService {

    private final Logger logger = LoggerFactory.getLogger(ChildAlertService.class);
    private final DataRepository dataRepository;

    /**
     * Constructeur du service avec injection de dépendance du repository.
     *
     * @param dataRepository le repository contenant les données de l'application
     */
    @Autowired
    public ChildAlertService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    /**
     * Récupère les enfants vivant à une adresse donnée.
     * Cette méthode retourne un ChildAlertDTO contenant :
     * - La liste des enfants (≤ 18 ans) avec leur âge
     * - La liste des autres membres du foyer (> 18 ans)
     *
     * @param address L'adresse à vérifier
     * @return Un ChildAlertDTO contenant les enfants et les autres membres du foyer
     * @throws RuntimeException si une erreur survient lors du traitement des données
     */
    public ChildAlertDTO getChildrenAtAddress(String address) {
        logger.info("Recherche des enfants à l'adresse: {}", address);

        try {
            // Récupérer les données une seule fois
            Data data = dataRepository.getData();
            List<Person> persons = data.getPersons();
            List<MedicalRecord> medicalRecords = data.getMedicalRecords();

            logger.debug("Nombre total de personnes: {}", persons.size());
            logger.debug("Nombre total de dossiers médicaux: {}", medicalRecords.size());

            // Créer une map des dossiers médicaux pour un accès rapide
            Map<String, MedicalRecord> medicalRecordsMap = medicalRecords.stream()
                    .collect(Collectors.toMap(
                            record -> record.getFirstName() + record.getLastName(),
                            record -> record
                    ));

            // Trouver toutes les personnes à cette adresse
            List<Person> personsAtAddress = persons.stream()
                    .filter(p -> p.getAddress().equals(address))
                    .collect(Collectors.toList());

            if (personsAtAddress.isEmpty()) {
                logger.info("Aucune personne trouvée à l'adresse: {}", address);
                return new ChildAlertDTO(); // Retourne un DTO vide
            }

            logger.info("Nombre de personnes trouvées à l'adresse {}: {}", address, personsAtAddress.size());

            // Initialisation des listes pour stocker les résultats
            List<ChildDTO> children = new ArrayList<>();
            List<HouseholdMemberDTO> householdMembers = new ArrayList<>();

            // Traitement de chaque personne trouvée à l'adresse
            for (Person person : personsAtAddress) {
                try {
                    // Récupération du dossier médical pour calculer l'âge
                    MedicalRecord record = medicalRecordsMap.get(person.getFirstName() + person.getLastName());
                    if (record == null) {
                        logger.warn("Dossier médical non trouvé pour: {} {}", person.getFirstName(), person.getLastName());
                        continue;
                    }

                    // Calcul de l'âge de la personne
                    int age = AgeCalculator.calculateAge(record.getBirthdate());
                    logger.debug("Âge calculé pour {} {}: {}", person.getFirstName(), person.getLastName(), age);

                    // Classification de la personne selon son âge
                    if (age <= 18) {
                        // Création du DTO pour un enfant
                        ChildDTO childDTO = new ChildDTO();
                        childDTO.setFirstName(person.getFirstName());
                        childDTO.setLastName(person.getLastName());
                        childDTO.setAge(age);
                        children.add(childDTO);
                        logger.debug("Enfant ajouté: {} {}, âge: {}",
                                childDTO.getFirstName(),
                                childDTO.getLastName(),
                                childDTO.getAge());
                    } else {
                        // Création du DTO pour un membre du foyer adulte
                        HouseholdMemberDTO memberDTO = new HouseholdMemberDTO();
                        memberDTO.setFirstName(person.getFirstName());
                        memberDTO.setLastName(person.getLastName());
                        householdMembers.add(memberDTO);
                        logger.debug("Membre du foyer ajouté: {} {}",
                                memberDTO.getFirstName(),
                                memberDTO.getLastName());
                    }
                } catch (Exception e) {
                    logger.error("Erreur lors du traitement de {} {}: {}",
                            person.getFirstName(),
                            person.getLastName(),
                            e.getMessage());
                }
            }

            // Construction de la réponse finale
            ChildAlertDTO response = new ChildAlertDTO();
            response.setChildren(children);
            response.setHouseholdMembers(householdMembers);

            logger.info("Résumé pour l'adresse {}: {} enfants, {} autres membres du foyer",
                    address, children.size(), householdMembers.size());

            return response;
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche des enfants à l'adresse {}: {}", address, e.getMessage());
            throw new RuntimeException("Erreur lors de la recherche des enfants: " + e.getMessage(), e);
        }
    }
}
