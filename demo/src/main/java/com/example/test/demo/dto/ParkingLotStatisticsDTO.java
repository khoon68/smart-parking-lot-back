package com.example.test.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLotStatisticsDTO {
    private Long parkingLotId;
    private String parkingLotName;
    private Long totalReservations;
    private Long totalRevenue;
}
