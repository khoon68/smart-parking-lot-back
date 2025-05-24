package com.example.test.demo.service;

import com.example.test.demo.dto.ParkingSlotDTO;
import com.example.test.demo.entity.ParkingSlot;
import com.example.test.demo.entity.Reservation;
import com.example.test.demo.repository.ParkingSlotRepository;
import com.example.test.demo.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.test.demo.util.TimeUtil.parseTimeSlotToDateTime;

@Service
@RequiredArgsConstructor
public class ParkingSlotServiceImpl implements ParkingSlotService{

    private final ParkingSlotRepository parkingSlotRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public List<ParkingSlotDTO> findAvailableSlots(Long parkingLotId, LocalDate date, List<String> timeSlots) {
        List<ParkingSlot> slots = parkingSlotRepository.findByParkingLotId(parkingLotId);
        if (slots.isEmpty()) return List.of();

        List<String> sortedTimeSlots = timeSlots.stream().sorted().toList();
        String startTimeSlot = sortedTimeSlots.get(0);
        String endTimeSlot = sortedTimeSlots.get(sortedTimeSlots.size() - 1);

        LocalDateTime start = parseTimeSlotToDateTime(date, startTimeSlot);
        LocalDateTime end = parseTimeSlotToDateTime(date, endTimeSlot).plusHours(1);

        List<Reservation> conflicts = reservationRepository.findConflictsByParkingLot(parkingLotId, start, end);
        Set<Long> reservedSlotIds = conflicts.stream()
                .map(r -> r.getParkingSlot().getId())
                .collect(Collectors.toSet());

        return slots.stream()
                .map(slot -> {
                    boolean isAvailable = !reservedSlotIds.contains(slot.getId());
                    return ParkingSlotDTO.from(slot, isAvailable);
                })
                .toList();
    }
}
