package com.example.test.demo.dto;

import com.example.test.demo.entity.ReservationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationResponseDTO {
    private Long reservationId;
    private String username;
    private String parkingLotName;
    private int slotNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int totalPrice;
    private ReservationStatus status;
}
