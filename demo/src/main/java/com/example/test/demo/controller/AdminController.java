package com.example.test.demo.controller;

import com.example.test.demo.repository.ParkingLotRepository;
import com.example.test.demo.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
public class AdminController {

    private final ReservationRepository reservationRepository;
    private final ParkingLotRepository parkingLotRepository;




}
