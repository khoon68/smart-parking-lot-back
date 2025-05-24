package com.example.test.demo.service;

import com.example.test.demo.dto.ParkingLotStatisticsDTO;
import com.example.test.demo.dto.ReservationResponseDTO;
import com.example.test.demo.entity.User;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

public interface ReservationService {
    List<ReservationResponseDTO> getReservationsByParkingLot(User user, Long parkingLotId) throws AccessDeniedException;

    List<ReservationResponseDTO> getAllReservations(User user) throws AccessDeniedException;

    ParkingLotStatisticsDTO getParkingLotStatistics(User user, Long parkingLotId) throws AccessDeniedException;

    List<ParkingLotStatisticsDTO> getAllParkingLotStatistics(User user) throws AccessDeniedException;
}
