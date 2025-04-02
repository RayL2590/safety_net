package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.CommunityEmailDTO;
import com.ryan.safetynet.alerts.service.CommunityEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller pour récupérer les emails de la communauté par ville.
 */
@RestController
@RequestMapping("/communityEmail")
public class CommunityEmailController {

    private final Logger logger = LoggerFactory.getLogger(CommunityEmailController.class);
    private final CommunityEmailService communityEmailService;

    @Autowired
    public CommunityEmailController(CommunityEmailService communityEmailService) {
        this.communityEmailService = communityEmailService;
    }

    /**
     * Endpoint pour récupérer les emails des habitants d'une ville.
     *
     * @param city Nom de la ville.
     * @return CommunityEmailDTO contenant la liste des emails.
     */
    @GetMapping
    public ResponseEntity<CommunityEmailDTO> getEmailsByCity(@RequestParam String city) {
        logger.info("Requête reçue pour la ville : {}", city);

        CommunityEmailDTO response = communityEmailService.getEmailsByCity(city);

        if (response.getEmails().isEmpty()) {
            logger.info("Aucun email trouvé pour la ville : {}", city);
            return ResponseEntity.ok().body(null);
        }

        logger.info("Nombre d'emails trouvés : {}", response.getEmails().size());

        return ResponseEntity.ok(response);
    }
}
