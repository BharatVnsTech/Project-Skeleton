package com.enterprise.platform.parking.controller;

import com.enterprise.platform.core.CorrelationConstants;
import com.enterprise.platform.logging.CorrelationIdContext;
import com.enterprise.platform.parking.model.ParkingSlot;
import com.enterprise.platform.parking.service.ParkingService;
import com.enterprise.platform.web.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parking")
public class ParkingController {

    private static final Logger log = LoggerFactory.getLogger(ParkingController.class);

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @GetMapping("/slots")
    public ResponseEntity<ApiResponse<List<ParkingSlot>>> getAllSlots(HttpServletRequest request) {
        String correlationId = resolveCorrelationId(request);
        log.info("GET /api/v1/parking/slots - correlationId={}", correlationId);

        List<ParkingSlot> slots = parkingService.getAllSlots();
        return ResponseEntity.ok(ApiResponse.success(slots, "Parking slots retrieved successfully", correlationId));
    }

    @GetMapping("/slots/{id}")
    public ResponseEntity<ApiResponse<ParkingSlot>> getSlotById(@PathVariable String id, HttpServletRequest request) {
        String correlationId = resolveCorrelationId(request);
        log.info("GET /api/v1/parking/slots/{} - correlationId={}", id, correlationId);

        ParkingSlot slot = parkingService.getSlotById(id);
        return ResponseEntity.ok(ApiResponse.success(slot, "Parking slot retrieved successfully", correlationId));
    }

    private String resolveCorrelationId(HttpServletRequest request) {
        String correlationId = request.getHeader(CorrelationConstants.CORRELATION_ID_HEADER);
        return CorrelationIdContext.getOrCreate(correlationId);
    }
}
