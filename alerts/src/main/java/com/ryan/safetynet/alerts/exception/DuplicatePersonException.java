package com.ryan.safetynet.alerts.exception;

public class DuplicatePersonException extends RuntimeException {
    public DuplicatePersonException(String firstName, String lastName, String address) {
        super(String.format("Une personne avec le prénom %s, le nom %s et l'adresse %s existe déjà", 
            firstName, lastName, address));
    }
}
