package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.PersonInfoDTO;
import com.ryan.safetynet.alerts.service.PersonInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller pour récupérer les informations détaillées d'une personne par son prénom et son nom de famille.
 */
@RestController
@RequestMapping("/personInfo")
public class PersonInfoController {

    private final Logger logger = LoggerFactory.getLogger(PersonInfoController.class);
    private final PersonInfoService personInfoService;

    @Autowired
    public PersonInfoController(PersonInfoService personInfoService) {
        this.personInfoService = personInfoService;
    }

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
        logger.info("Requête reçue pour la personne : {} {}", firstName, lastName);

        PersonInfoDTO response = personInfoService.getPersonInfo(firstName, lastName);

        if (response == null) {
            logger.info("Aucune personne trouvée : {} {}", firstName, lastName);
            return ResponseEntity.ok().body(null);
        }

        logger.info("Personne trouvée : {} {}", firstName, lastName);
        return ResponseEntity.ok(response);
    }
}
