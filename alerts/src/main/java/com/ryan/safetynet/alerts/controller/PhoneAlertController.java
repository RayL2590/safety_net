package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.PhoneAlertDTO;
import com.ryan.safetynet.alerts.service.PhoneAlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller gérant les alertes liées aux numéros de téléphone.
 * Expose l'endpoint /phoneAlert qui permet de récupérer la liste des numéros de téléphone
 * pour envoyer des messages d'urgence à des foyers spécifiques.
 */
@RestController
@RequestMapping("/phoneAlert")
public class PhoneAlertController {

    private final Logger logger = LoggerFactory.getLogger(PhoneAlertController.class);
    private final PhoneAlertService phoneAlertService;

    @Autowired
    public PhoneAlertController(PhoneAlertService phoneAlertService) {
        this.phoneAlertService = phoneAlertService;
    }

    /**
     * Endpoint pour récupérer la liste des numéros de téléphone couverts par une caserne donnée.
     *
     * @param stationNumber Le numéro de la station de pompiers.
     * @return ResponseEntity contenant la liste des numéros de téléphone (PhoneAlertDTO).
     */
    @GetMapping
    public ResponseEntity<PhoneAlertDTO> getPhoneNumbersByStation(@RequestParam int stationNumber) {
        logger.info("Requête reçue pour les numéros de téléphone couverts par la station : {}", stationNumber);

        List<String> phoneNumbers = phoneAlertService.getPhoneNumbersByStation(stationNumber);

        if (phoneNumbers.isEmpty()) {
            logger.info("Aucun numéro de téléphone trouvé pour la station : {}", stationNumber);
            return ResponseEntity.ok().body(null);
        }

        logger.info("Nombre de numéros de téléphone trouvés : {}", phoneNumbers.size());

        PhoneAlertDTO response = new PhoneAlertDTO();
        response.setPhoneNumbers(phoneNumbers);

        return ResponseEntity.ok(response);
    }
}
