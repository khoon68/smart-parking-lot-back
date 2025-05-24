package com.example.test.demo.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReservationRequestDTO {
    private Long slotId;
    private List<String> timeSlots;
}
