package com.enterprise.platform.parking.repository;

import com.enterprise.platform.parking.model.ParkingSlot;
import com.enterprise.platform.parking.model.ParkingSlot.Status;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ParkingSlotRepository {

    private static final List<ParkingSlot> PARKING_SLOTS = List.of(
            ParkingSlot.builder().id("P-101").location("Block A").status(Status.AVAILABLE.name()).build(),
            ParkingSlot.builder().id("P-102").location("Block A").status(Status.OCCUPIED.name()).build(),
            ParkingSlot.builder().id("P-103").location("Block B").status(Status.AVAILABLE.name()).build(),
            ParkingSlot.builder().id("P-104").location("Block B").status(Status.RESERVED.name()).build(),
            ParkingSlot.builder().id("P-105").location("Block C").status(Status.AVAILABLE.name()).build(),
            ParkingSlot.builder().id("P-106").location("Block C").status(Status.OCCUPIED.name()).build(),
            ParkingSlot.builder().id("P-107").location("Block A").status(Status.AVAILABLE.name()).build(),
            ParkingSlot.builder().id("P-108").location("Block D").status(Status.RESERVED.name()).build()
    );

    public List<ParkingSlot> findAll() {
        return PARKING_SLOTS;
    }

    public Optional<ParkingSlot> findById(String id) {
        return PARKING_SLOTS.stream()
                .filter(slot -> slot.getId().equals(id))
                .findFirst();
    }
}
