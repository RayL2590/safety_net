package com.ryan.safetynet.alerts.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException fireStationNotFound(String stationNumber) {
        return new ResourceNotFoundException("La caserne de pompiers numéro " + stationNumber + " n'existe pas");
    }

    public static ResourceNotFoundException noResidentsAtAddress(String address) {
        return new ResourceNotFoundException("Aucun résident trouvé à l'adresse " + address);
    }

    public static ResourceNotFoundException noMedicalInfo(String firstName, String lastName) {
        return new ResourceNotFoundException("Aucune information médicale disponible pour " + firstName + " " + lastName);
    }

    public static ResourceNotFoundException noChildrenAtAddress(String address) {
        return new ResourceNotFoundException("Aucun enfant trouvé à l'adresse " + address);
    }
    
} 