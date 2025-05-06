package com.ryan.safetynet.alerts.service;

import com.ryan.safetynet.alerts.dto.FireStationDTO;
import com.ryan.safetynet.alerts.dto.PersonDTO;
import com.ryan.safetynet.alerts.model.Data;
import com.ryan.safetynet.alerts.model.MedicalRecord;
import com.ryan.safetynet.alerts.model.Person;
import com.ryan.safetynet.alerts.repository.DataRepository;
import com.ryan.safetynet.alerts.utils.AgeCalculator;
import com.ryan.safetynet.alerts.utils.MedicalRecordUtils;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FireStationCoverageService {

    private final DataRepository dataRepository;
    private final FireStationService fireStationService;

    /**
     * Récupère les personnes couvertes par une station de pompiers.
     *
     * @param stationNumber Le numéro de la station de pompiers.
     * @return Une map contenant la liste des personnes et le décompte des adultes et enfants.
     * @throws ResourceNotFoundException si la station n'existe pas
     */
    public FireStationDTO getPersonsCoveredByStation(int stationNumber) {
        // Vérifier si la station existe
        if (!fireStationService.existsByStationNumber(String.valueOf(stationNumber))) {
            throw new ResourceNotFoundException("La station de pompiers " + stationNumber + " n'existe pas");
        }

        // Récupérer les données une seule fois
        Data data = dataRepository.getData();
        List<Person> persons = data.getPersons();
        List<MedicalRecord> medicalRecords = data.getMedicalRecords();

        log.debug("Recherche des personnes couvertes par la station n° {}", stationNumber);
        log.debug("Nombre total de personnes: {}", persons.size());
        log.debug("Nombre total de dossiers médicaux: {}", medicalRecords.size());

        // Trouver les adresses couvertes par la station en utilisant le service centralisé
        List<String> addresses = fireStationService.getAddressesCoveredByStation(stationNumber);

        log.debug("Adresses couvertes par la station {}: {}", stationNumber, addresses);

        // Filtrer les personnes vivant à ces adresses
        List<PersonDTO> coveredPersons = persons.stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .map(p -> {
                    log.debug("Personne trouvée à l'adresse couverte: {} {} à {}",
                            p.getFirstName(), p.getLastName(), p.getAddress());

                    int calculatedAge = AgeCalculator.calculateAge(
                            MedicalRecordUtils.getBirthdate(p.getFirstName(), p.getLastName(), medicalRecords)
                    );

                    log.debug("Âge calculé pour {} {}: {}", p.getFirstName(), p.getLastName(), calculatedAge);

                    PersonDTO personDTO = new PersonDTO();
                    personDTO.setFirstName(p.getFirstName());
                    personDTO.setLastName(p.getLastName());
                    personDTO.setAddress(p.getAddress());
                    personDTO.setPhone(p.getPhone());
                    personDTO.setAge(calculatedAge);

                    return personDTO;
                })
                .collect(Collectors.toList());

        log.debug("Nombre de personnes couvertes trouvées: {}", coveredPersons.size());

        // Calculer le nombre d'adultes et d'enfants
        long childCount = coveredPersons.stream()
                .filter(p -> p.getAge() <= 18)
                .count();
        long adultCount = coveredPersons.size() - childCount;

        log.debug("Nombre d'adultes: {}, Nombre d'enfants: {}", adultCount, childCount);

        // Construire la réponse
        FireStationDTO response = new FireStationDTO();
        response.setPersons(coveredPersons);
        response.setAdultCount(adultCount);
        response.setChildCount(childCount);

        return response;
    }

}
