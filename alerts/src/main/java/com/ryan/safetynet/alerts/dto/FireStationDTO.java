package com.ryan.safetynet.alerts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

/**
 * DTO représentant les informations relatives à une caserne de pompiers et les personnes qu'elle dessert.
 * Utilisé pour l'endpoint /firestation?stationNumber=X qui retourne la liste des personnes couvertes
 * par la caserne et le décompte des adultes et des enfants.
 */
@Getter
@Setter
@ToString
public class FireStationDTO {
    @JsonProperty("persons")
    private List<PersonDTO> persons;
    
    @JsonProperty("adultCount")
    private long adultCount;        
    
    @JsonProperty("childCount")
    private long childCount;       
}

