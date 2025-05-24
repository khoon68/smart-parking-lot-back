package com.example.test.demo.service;

import com.example.test.demo.dto.ParkingLotStatisticsDTO;
import com.example.test.demo.dto.ReservationResponseDTO;
import com.example.test.demo.entity.*;
import com.example.test.demo.repository.ParkingLotRepository;
import com.example.test.demo.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ParkingLotRepository spaceRepository;

    @Override
    public List<ReservationResponseDTO> getReservationsByParkingLot(User user, Long parkingLotId) throws AccessDeniedException {

        ParkingLot parkingLot = spaceRepository.findById(parkingLotId)
                .orElseThrow(() -> new RuntimeException("해당 주차 공간이 존재하지 않습니다."));

        if (!parkingLot.getOwner().getId().equals(user.getId()) && !user.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        List<Reservation> reservations = reservationRepository.findByParkingSlot_ParkingLot_Id(parkingLotId);

        return reservations.stream().map(reservation -> {
            ReservationResponseDTO dto = new ReservationResponseDTO();
            dto.setReservationId(reservation.getId());
            dto.setUsername(reservation.getUser().getUsername());
            dto.setSlotNumber(reservation.getParkingSlot().getSlotNumber());
            dto.setParkingLotName(parkingLot.getName());
            dto.setStartTime(reservation.getStartTime());
            dto.setEndTime(reservation.getEndTime());
            dto.setTotalPrice(reservation.getTotalPrice());
            dto.setStatus(reservation.getStatus());
            return dto;
        }).toList();
    }

    @Override
    public List<ReservationResponseDTO> getAllReservations(User user) throws AccessDeniedException {
        if (!user.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream().map(reservation -> {
            ReservationResponseDTO dto = new ReservationResponseDTO();
            dto.setReservationId(reservation.getId());
            dto.setUsername(reservation.getUser().getUsername());
            dto.setSlotNumber(reservation.getParkingSlot().getSlotNumber());
            dto.setParkingLotName(reservation.getParkingSlot().getParkingLot().getName());
            dto.setStartTime(reservation.getStartTime());
            dto.setEndTime(reservation.getEndTime());
            dto.setTotalPrice(reservation.getTotalPrice());
            dto.setStatus(reservation.getStatus());
            return dto;
        }).toList();
    }

    @Override
    public ParkingLotStatisticsDTO getParkingLotStatistics(User user, Long parkingLotId) throws AccessDeniedException {
        ParkingLot parkingLot = spaceRepository.findById(parkingLotId)
                .orElseThrow(() -> new RuntimeException("해당 주차 공간이 존재하지 않습니다."));

        if (!parkingLot.getOwner().getId().equals(user.getId()) && !user.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        List<Object[]> statsList = reservationRepository.getReservationStatsByParkingLotId(parkingLotId);
        Object[] stats = statsList.isEmpty() ? new Object[]{0L, 0L} : statsList.get(0);

        long totalReservations = ((Number) stats[0]).longValue();
        long totalRevenue= stats[1] != null ? ((Number) stats[1]).longValue() : 0L;

        ParkingLotStatisticsDTO dto = new ParkingLotStatisticsDTO(
                parkingLot.getId(),
                parkingLot.getName(),
                totalReservations,
                totalRevenue
        );

        return dto;
    }

    @Override
    public List<ParkingLotStatisticsDTO> getAllParkingLotStatistics(User user) throws AccessDeniedException {
        if (!user.getRole().equals(UserRole.ADMIN)) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        return reservationRepository.findAllStatisticsByParkingLot();
    }
}
