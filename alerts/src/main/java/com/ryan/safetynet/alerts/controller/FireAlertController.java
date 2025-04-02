package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.FireAlertDTO;
import com.ryan.safetynet.alerts.service.FireAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller gérant les alertes incendie.
 */
@RestController
@RequestMapping("/fire")
public class FireAlertController {

    private final FireAlertService fireAlertService;

    @Autowired
    public FireAlertController(FireAlertService fireAlertService) {
        this.fireAlertService = fireAlertService;
    }

    /**
     * Endpoint pour récupérer les habitants d'une adresse avec leurs informations médicales et la caserne associée.
     *
     * @param address l'adresse à vérifier
     * @return FireAlertDTO avec détails des résidents et la station de pompiers
     */
    @GetMapping
    public ResponseEntity<FireAlertDTO> getResidentsByAddress(@RequestParam String address) {
        FireAlertDTO response = fireAlertService.getPersonsAndFireStationByAddress(address);

        if (response.getResidents().isEmpty()) {
            return ResponseEntity.ok().body(null);
        }

        return ResponseEntity.ok(response);
    }

}
