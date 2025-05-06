package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.ChildAlertDTO;
import com.ryan.safetynet.alerts.dto.ChildDTO;
import com.ryan.safetynet.alerts.dto.HouseholdMemberDTO;
import com.ryan.safetynet.alerts.dto.PersonWithMedicalInfoDTO;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import com.ryan.safetynet.alerts.utils.MedicalRecordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service gérant les alertes concernant les enfants.
 * Ce service permet de récupérer les informations sur les enfants (≤ 18 ans)
 * vivant à une adresse donnée, ainsi que les informations sur les autres
 * membres du foyer. Cette fonctionnalité est utile pour les services d'urgence
 * qui doivent identifier rapidement la présence d'enfants dans un foyer.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ChildAlertService {

    private final DataRepository dataRepository;
    private final PersonService personService;

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
        log.info("Recherche des enfants à l'adresse: {}", address);

        try {
            // Récupérer les données une seule fois
            Data data = dataRepository.getData();
            List<MedicalRecord> medicalRecords = data.getMedicalRecords();
            log.debug("Nombre total de dossiers médicaux: {}", medicalRecords.size());

            // Trouver toutes les personnes à cette adresse en utilisant PersonService
            List<Person> personsAtAddress = personService.getPersonsByAddress(address);

            // Initialisation des listes pour stocker les résultats
            List<ChildDTO> children = new ArrayList<>();
            List<HouseholdMemberDTO> householdMembers = new ArrayList<>();

            if (personsAtAddress.isEmpty()) {
                log.info("Aucune personne trouvée à l'adresse: {}", address);
                ChildAlertDTO response = new ChildAlertDTO();
                response.setChildren(children);
                response.setHouseholdMembers(householdMembers);
                return response;
            }

            log.info("Nombre de personnes trouvées à l'adresse {}: {}", address, personsAtAddress.size());

            // Traitement de chaque personne trouvée à l'adresse
            for (Person person : personsAtAddress) {
                try {
                    // Récupération du dossier médical pour calculer l'âge
                    PersonWithMedicalInfoDTO medicalInfo = MedicalRecordUtils.extractMedicalInfo(person, medicalRecords);
                    int age = medicalInfo.getAge();
                    log.debug("Âge calculé pour {} {}: {}", person.getFirstName(), person.getLastName(), age);

                    // Classification de la personne selon son âge
                    if (age <= 18) {
                        // Création du DTO pour un enfant
                        ChildDTO childDTO = new ChildDTO();
                        childDTO.setFirstName(person.getFirstName());
                        childDTO.setLastName(person.getLastName());
                        childDTO.setAge(age);
                        children.add(childDTO);
                        log.debug("Enfant ajouté: {} {}, âge: {}",
                                childDTO.getFirstName(),
                                childDTO.getLastName(),
                                childDTO.getAge());
                    } else {
                        // Création du DTO pour un membre du foyer adulte
                        HouseholdMemberDTO memberDTO = new HouseholdMemberDTO();
                        memberDTO.setFirstName(person.getFirstName());
                        memberDTO.setLastName(person.getLastName());
                        householdMembers.add(memberDTO);
                        log.debug("Membre du foyer ajouté: {} {}",
                                memberDTO.getFirstName(),
                                memberDTO.getLastName());
                    }
                } catch (Exception e) {
                    log.error("Erreur lors du traitement de {} {}: {}",
                            person.getFirstName(),
                            person.getLastName(),
                            e.getMessage());
                }
            }

            // Construction de la réponse finale
            ChildAlertDTO response = new ChildAlertDTO();
            response.setChildren(children);
            response.setHouseholdMembers(householdMembers);

            log.info("Résumé pour l'adresse {}: {} enfants, {} autres membres du foyer",
                    address, children.size(), householdMembers.size());

            return response;
        } catch (Exception e) {
            log.error("Erreur lors de la recherche des enfants à l'adresse {}: {}", address, e.getMessage());
            throw new RuntimeException("Erreur lors de la recherche des enfants: " + e.getMessage(), e);
        }
    }
}
