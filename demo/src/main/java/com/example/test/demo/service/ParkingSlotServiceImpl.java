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
        if (slots.isEmpty()) {
            System.out.println("⚠️ 해당 주차공간에 슬롯이 없습니다. parkingLotId = " + parkingLotId);
            return List.of();
        }

        List<String> sortedTimeSlots = timeSlots.stream().sorted().toList();
        String startTimeSlot = sortedTimeSlots.get(0);
        String endTimeSlot = sortedTimeSlots.get(sortedTimeSlots.size() - 1);

        LocalDateTime start = parseTimeSlotToDateTime(date, startTimeSlot);
        LocalDateTime end = parseTimeSlotToDateTime(date, endTimeSlot).plusHours(1);

        System.out.println("\n===== [🔍 예약 충돌 검사 로그] =====");
        System.out.println("🗓️ 요청 날짜: " + date);
        System.out.println("⏰ 요청 슬롯 리스트: " + timeSlots);
        System.out.println("📌 정렬된 timeSlots: " + sortedTimeSlots);
        System.out.println("⏱️ 검사 시간 범위: " + start + " ~ " + end);
        System.out.println("====================================\n");

        List<Reservation> conflicts = reservationRepository.findConflictsByParkingLot(parkingLotId, start, end);
        System.out.println("🚧 충돌된 예약 개수: " + conflicts.size());

        for (Reservation r : conflicts) {
            System.out.println("→ 예약 ID: " + r.getId()
                    + " | 상태: " + r.getStatus()
                    + " | 시간: " + r.getStartTime() + " ~ " + r.getEndTime()
                    + " | 슬롯 ID: " + r.getParkingSlot().getId());
        }

        Set<Long> reservedSlotIds = conflicts.stream()
                .map(r -> r.getParkingSlot().getId())
                .collect(Collectors.toSet());

        List<ParkingSlotDTO> result = slots.stream()
                .map(slot -> {
                    boolean isAvailable = !reservedSlotIds.contains(slot.getId());
                    System.out.println("🟦 슬롯 ID " + slot.getId()
                            + " (" + slot.getSlotNumber() + "번) → "
                            + (isAvailable ? "✅ 사용 가능" : "❌ 예약 중"));
                    return ParkingSlotDTO.from(slot, isAvailable);
                })
                .toList();

        System.out.println("===== [✅ 처리 완료] 총 반환 슬롯: " + result.size() + "\n");
        return result;
    }
}
