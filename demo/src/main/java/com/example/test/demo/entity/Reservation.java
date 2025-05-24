package com.example.test.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private ParkingSlot parkingSlot;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
}
