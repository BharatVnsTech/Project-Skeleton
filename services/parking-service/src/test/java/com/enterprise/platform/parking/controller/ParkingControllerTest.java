package com.enterprise.platform.parking.controller;

import com.enterprise.platform.parking.model.ParkingSlot;
import com.enterprise.platform.parking.service.ParkingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ParkingController.class)
class ParkingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ParkingService parkingService;

    @Test
    void getAllSlots_shouldReturnSlots() throws Exception {
        ParkingSlot slot1 = ParkingSlot.builder().id("P-101").location("Block A").status("AVAILABLE").build();
        ParkingSlot slot2 = ParkingSlot.builder().id("P-102").location("Block A").status("OCCUPIED").build();

        when(parkingService.getAllSlots()).thenReturn(List.of(slot1, slot2));

        mockMvc.perform(get("/api/v1/parking/slots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value("P-101"))
                .andExpect(jsonPath("$.data[0].status").value("AVAILABLE"));
    }

    @Test
    void getSlotById_shouldReturnSlot() throws Exception {
        ParkingSlot slot = ParkingSlot.builder().id("P-101").location("Block A").status("AVAILABLE").build();
        when(parkingService.getSlotById("P-101")).thenReturn(slot);

        mockMvc.perform(get("/api/v1/parking/slots/P-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("P-101"))
                .andExpect(jsonPath("$.data.location").value("Block A"));
    }
}
