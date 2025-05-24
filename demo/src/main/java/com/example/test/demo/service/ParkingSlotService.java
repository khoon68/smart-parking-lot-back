package com.example.test.demo.service;

import com.example.test.demo.dto.ParkingSlotDTO;

import java.time.LocalDate;
import java.util.List;

public interface ParkingSlotService {
    List<ParkingSlotDTO> findAvailableSlots(
            Long parkingLotId,
            LocalDate date,
            List<String> timeSlots
    );
}
