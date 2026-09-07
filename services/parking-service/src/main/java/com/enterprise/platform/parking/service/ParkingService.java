package com.enterprise.platform.parking.service;

import com.enterprise.platform.exception.ErrorCode;
import com.enterprise.platform.exception.ResourceNotFoundException;
import com.enterprise.platform.parking.model.ParkingSlot;
import com.enterprise.platform.parking.repository.ParkingSlotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingService {

    private static final Logger log = LoggerFactory.getLogger(ParkingService.class);

    private final ParkingSlotRepository parkingSlotRepository;

    public ParkingService(ParkingSlotRepository parkingSlotRepository) {
        this.parkingSlotRepository = parkingSlotRepository;
    }

    public List<ParkingSlot> getAllSlots() {
        log.info("Fetching all parking slots");
        List<ParkingSlot> slots = parkingSlotRepository.findAll();
        log.info("Found {} parking slots", slots.size());
        return slots;
    }

    public ParkingSlot getSlotById(String id) {
        log.info("Fetching parking slot with id: {}", id);
        return parkingSlotRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Parking slot not found with id: {}", id);
                    return new ResourceNotFoundException(ErrorCode.PARKING_SLOT_NOT_FOUND,
                            "Parking slot not found with id: " + id);
                });
    }
}
