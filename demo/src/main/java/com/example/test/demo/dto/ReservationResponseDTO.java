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
    private Long slotId;
    private int slotNumber;
    private String startTime;
    private String endTime;
    private int totalPrice;
    private ReservationStatus status;
    private boolean isSlotOpened;
}
