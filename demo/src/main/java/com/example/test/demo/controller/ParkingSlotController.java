package com.example.test.demo.controller;

import com.example.test.demo.dto.ParkingSlotDTO;
import com.example.test.demo.entity.ParkingSlot;
import com.example.test.demo.repository.ParkingSlotRepository;
import com.example.test.demo.service.ParkingSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/parking-lots")
@RequiredArgsConstructor
public class ParkingSlotController {

    private final ParkingSlotRepository slotRepository;
    private final ParkingSlotService parkingSlotService;

    @GetMapping("/{parkingLotId}/slots")
    public ResponseEntity<List<ParkingSlotDTO>> getSlots(@PathVariable Long parkingLotId) {
        List<ParkingSlot> slots = slotRepository.findByParkingLotId(parkingLotId);
        List<ParkingSlotDTO> dtos = slots.stream()
                .map(s -> new ParkingSlotDTO(
                        s.getId(),
                        s.getSlotNumber(),
                        s.isAvailable(),
                        s.isOpened()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{parkingLotId}/available-slots")
    public ResponseEntity<List<ParkingSlotDTO>> getAvailableSlots(
            @PathVariable Long parkingLotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam List<String> timeSlots
    ) {
        List<ParkingSlotDTO> result = parkingSlotService.findAvailableSlots(parkingLotId, date, timeSlots);
        return ResponseEntity.ok(result);
    }
}
