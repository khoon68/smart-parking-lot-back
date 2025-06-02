package com.example.test.demo.controller;

import com.example.test.demo.dto.ParkingLotDTO;
import com.example.test.demo.entity.ParkingLot;
import com.example.test.demo.repository.ParkingLotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/parking-lots")
@RequiredArgsConstructor
public class ParkingLotController {

    private final ParkingLotRepository parkingLotRepository;

    @GetMapping
    public ResponseEntity<List<ParkingLotDTO>> getAvailableParkingSpaces() {
        List<ParkingLot> parkingLots = parkingLotRepository.findByAvailableSlotsGreaterThan(0);
        List<ParkingLotDTO> dtos = parkingLots.stream().map(
                parkingLot -> new ParkingLotDTO(
                        parkingLot.getId(),
                        parkingLot.getName(),
                        parkingLot.getDistance(),
                        parkingLot.getPricePerHour(),
                        parkingLot.getTotalSlots(),
                        parkingLot.getAvailableSlots()
                )
        ).toList();

        return ResponseEntity.ok(dtos);
    }

}
