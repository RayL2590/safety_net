package com.ryan.safetynet.alerts.controller;

import com.ryan.safetynet.alerts.dto.CommunityEmailDTO;
import com.ryan.safetynet.alerts.service.CommunityEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller pour récupérer les emails de la communauté par ville.
 * <p>
 * Expose un endpoint GET /communityEmail qui retourne la liste des emails
 * des habitants d'une ville spécifiée via le paramètre 'city'.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/communityEmail")
public class CommunityEmailController {

    private final CommunityEmailService communityEmailService;

    /**
     * Endpoint pour récupérer les emails des habitants d'une ville.
     *
     * @param city Nom de la ville (paramètre obligatoire)
     * @return ResponseEntity contenant soit :
     *         - HTTP 200 avec CommunityEmailDTO contenant les emails
     *         - HTTP 200 avec null si aucun email trouvé
     */
    @GetMapping
    public ResponseEntity<CommunityEmailDTO> getEmailsByCity(@RequestParam String city) {
        log.info("Requête reçue pour la ville : {}", city);

        CommunityEmailDTO response = communityEmailService.getEmailsByCity(city);

        if (response.getEmails().isEmpty()) {
            log.info("Aucun email trouvé pour la ville : {}", city);
            return ResponseEntity.ok().body(null);
        }

        log.info("Nombre d'emails trouvés : {}", response.getEmails().size());
        return ResponseEntity.ok(response);
    }
}
