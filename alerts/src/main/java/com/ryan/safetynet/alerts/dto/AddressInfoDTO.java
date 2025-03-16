package com.ryan.safetynet.alerts.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * DTO représentant les informations relatives à une adresse spécifique et ses résidents.
 * Utilisé principalement dans le cadre de l'endpoint /flood/stations qui regroupe les personnes par adresse
 * pour chaque station de pompiers spécifiée, afin de faciliter l'organisation des secours en cas d'inondation.
 * Contient l'adresse et la liste des résidents avec leurs informations médicales.
 */
@Getter
@Setter
public class AddressInfoDTO {
    private String address;
    private List<PersonWithMedicalInfoDTO> residents;
}