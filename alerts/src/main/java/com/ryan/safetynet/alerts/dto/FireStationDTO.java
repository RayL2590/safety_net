package com.ryan.safetynet.alerts.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * DTO représentant les informations relatives à une caserne de pompiers et les personnes qu'elle dessert.
 * Utilisé pour l'endpoint /firestation?stationNumber=X qui retourne la liste des personnes couvertes
 * par la caserne et le décompte des adultes et des enfants.
 */
@Getter
@Setter
public class FireStationDTO {
    private List<PersonDTO> persons;
    private long adultCount;        
    private long childCount;       
}

