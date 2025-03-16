package com.ryan.safetynet.alerts.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * DTO représentant les informations pour l'alerte d'inondation.
 * Utilisé pour l'endpoint /flood/stations?stations=X,Y,Z qui regroupe les personnes par adresse
 * pour chaque station de pompiers spécifiée. Cette structure facilite l'organisation des secours
 * en cas d'inondation, permettant aux services d'urgence de cibler efficacement les foyers affectés
 * et de connaître les besoins médicaux spécifiques des résidents.
 */
@Getter
@Setter
public class FloodStationDTO {
    private List<AddressInfoDTO> addresses;
}