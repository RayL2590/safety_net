package com.ryan.safetynet.alerts.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FireStation {
    private String address;
    private String station;

    @Override
    public String toString() {
        return "FireStation{" +
                "address='" + address + '\'' +
                ", station='" + station + '\'' +
                '}';
    }

}
