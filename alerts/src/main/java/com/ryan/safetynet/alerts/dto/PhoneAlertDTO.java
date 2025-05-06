package com.ryan.safetynet.alerts.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

/**
 * DTO contenant une liste de numéros de téléphone.
 * Utilisé pour l'endpoint /phoneAlert?firestation=X qui retourne les numéros de téléphone
 * des personnes desservies par la caserne spécifiée.
 */
@Getter
@Setter
@ToString
public class PhoneAlertDTO {
    private List<String> phoneNumbers;
}
