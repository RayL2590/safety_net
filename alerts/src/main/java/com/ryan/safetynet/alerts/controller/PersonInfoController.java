package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.PersonInfoDTO;
import com.ryan.safetynet.alerts.service.PersonInfoService;
import com.ryan.safetynet.alerts.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour récupérer les informations détaillées d'une personne par son prénom et son nom de famille.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/personInfo")
public class PersonInfoController {

    private final PersonInfoService personInfoService;

    /**
     * Endpoint pour récupérer les informations complètes d'une personne par son prénom et son nom de famille.
     *
     * @param firstName prénom de la personne
     * @param lastName nom de famille de la personne
     * @return les informations détaillées de la personne
     */
    @GetMapping
    public ResponseEntity<PersonInfoDTO> getPersonInfo(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        log.info("Requête reçue pour la personne : {} {}", firstName, lastName);

        PersonInfoDTO response = personInfoService.getPersonInfo(firstName, lastName);

        if (response == null) {
            log.info("Aucune personne trouvée : {} {}", firstName, lastName);
            throw new ResourceNotFoundException(
                String.format("La personne %s %s n'existe pas dans le système", firstName, lastName)
            );
        }

        log.info("Personne trouvée : {} {}", firstName, lastName);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint pour récupérer les informations de toutes les personnes portant un nom de famille spécifique.
     *
     * @param lastName nom de famille des personnes à rechercher
     * @return liste des informations des personnes trouvées
     */
    @GetMapping("/byLastName")
    public ResponseEntity<List<PersonInfoDTO>> getPersonsByLastName(@RequestParam String lastName) {
        log.info("Requête reçue pour les personnes avec le nom de famille : {}", lastName);

        List<PersonInfoDTO> response = personInfoService.getPersonsByLastName(lastName);

        if (response.isEmpty()) {
            log.info("Aucune personne trouvée avec le nom de famille : {}", lastName);
            throw new ResourceNotFoundException(
                String.format("Aucune personne avec le nom de famille %s n'existe dans le système", lastName)
            );
        }

        log.info("{} personnes trouvées avec le nom de famille : {}", response.size(), lastName);
        return ResponseEntity.ok(response);
    }
}
