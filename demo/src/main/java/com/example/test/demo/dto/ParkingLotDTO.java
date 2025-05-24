package com.example.test.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ParkingLotDTO {
    private Long id;
    private String name;
    private int distant;
    private int pricePerHour;
    private int totalSlots;
    private int availableSlots;
}
