package com.example.test.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parking_lots")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParkingLot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int distant;
    private int pricePerHour;
    private int totalSlots;
    private int availableSlots;

    @ManyToOne
    private User owner;
}
