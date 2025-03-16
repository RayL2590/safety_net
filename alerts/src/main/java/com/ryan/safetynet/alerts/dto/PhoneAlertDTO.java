package com.ryan.safetynet.alerts.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * DTO contenant une liste de numéros de téléphone.
 * Utilisé pour l'endpoint /phoneAlert?firestation=X qui retourne les numéros de téléphone
 * des personnes desservies par la caserne spécifiée.
 */
@Getter
@Setter
public class PhoneAlertDTO {
    private List<String> phoneNumbers;
}