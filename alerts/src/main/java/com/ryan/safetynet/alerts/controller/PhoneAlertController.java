package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.PhoneAlertDTO;
import com.ryan.safetynet.alerts.service.PhoneAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/phoneAlert")
public class PhoneAlertController {

    private final PhoneAlertService phoneAlertService;

    /**
     * Endpoint pour récupérer la liste des numéros de téléphone couverts par une caserne donnée.
     *
     * @param firestation Le numéro de la station de pompiers (obligatoire).
     * @return ResponseEntity contenant la liste des numéros de téléphone (PhoneAlertDTO).
     */
    @GetMapping
    public ResponseEntity<PhoneAlertDTO> getPhoneNumbersByStation(@RequestParam int firestation) {
        log.info("Requête reçue pour les numéros de téléphone couverts par la station : {}", firestation);

        List<String> phoneNumbers = phoneAlertService.getPhoneNumbersByStation(firestation);

        if (phoneNumbers.isEmpty()) {
            log.info("Aucun numéro de téléphone trouvé pour la station : {}", firestation);
            return ResponseEntity.ok().body(null);
        }

        log.info("Nombre de numéros de téléphone trouvés : {}", phoneNumbers.size());

        PhoneAlertDTO response = new PhoneAlertDTO();
        response.setPhoneNumbers(phoneNumbers);

        return ResponseEntity.ok(response);
    }
}
