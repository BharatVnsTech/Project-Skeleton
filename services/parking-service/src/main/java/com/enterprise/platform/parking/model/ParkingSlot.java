package com.enterprise.platform.parking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSlot {

    private String id;
    private String location;
    private String status;

    public enum Status {
        AVAILABLE, OCCUPIED, RESERVED
    }
}
