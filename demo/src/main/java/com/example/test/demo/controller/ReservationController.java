package com.example.test.demo.controller;

import com.example.test.demo.config.CustomUserDetails;
import com.example.test.demo.dto.BarrierOpenRequestDTO;
import com.example.test.demo.dto.ParkingLotStatisticsDTO;
import com.example.test.demo.dto.ReservationRequestDTO;
import com.example.test.demo.dto.ReservationResponseDTO;
import com.example.test.demo.entity.*;
import com.example.test.demo.repository.ParkingSlotRepository;
import com.example.test.demo.repository.ParkingLotRepository;
import com.example.test.demo.repository.ReservationRepository;
import com.example.test.demo.service.ReservationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationRepository reservationRepository;
    private final ParkingSlotRepository slotRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final ReservationServiceImpl reservationServiceImpl;

    @PostMapping
    public ResponseEntity<?> createReservation(
            @RequestBody ReservationRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = customUserDetails.getUser();
        System.out.println("요청 timeSlots: " + dto.getTimeSlots());

        ParkingSlot slot = slotRepository.findById(dto.getSlotId()).orElseThrow(
                () -> new RuntimeException("해당 주차칸을 찾을 수 없습니다.")
        );

        if (dto.getTimeSlots() == null || dto.getTimeSlots().isEmpty()) {
            return ResponseEntity.badRequest().body("예약할 시간 슬롯이 없습니다.");
        }

        List<String> timeLabels = dto.getTimeSlots().stream().sorted().toList();
        String startLabel = timeLabels.get(0);
        String endLabel = timeLabels.get(timeLabels.size() - 1);

        LocalDateTime nowDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startTime = parseLabelToTime(nowDate, startLabel);
        LocalDateTime endTime = parseLabelToTime(nowDate, endLabel).plusHours(1);

        //  예약 중복 체크
        boolean hasConfilict = !reservationRepository.findConflictsBySlot(
                slot.getId(), startTime, endTime
        ).isEmpty();

        if (hasConfilict) return ResponseEntity.badRequest().body("해당 시간에 이미 예약이 존재합니다.");

        // 요금 계산
        long hours = Duration.between(startTime, endTime).toHours();
        int price = (int) hours * slot.getParkingLot().getPricePerHour();

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setParkingSlot(slot);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setTotalPrice(price);
        reservation.setStatus(ReservationStatus.RESERVED);

        reservationRepository.save(reservation);

        slot.setAvailable(false);
        slotRepository.save(slot);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        ReservationResponseDTO response = new ReservationResponseDTO();
        response.setReservationId(reservation.getId());
        response.setUsername(user.getUsername());
        response.setSlotId(slot.getId());
        response.setSlotNumber(slot.getSlotNumber());
        response.setParkingLotName(slot.getParkingLot().getName());
        response.setStartTime(startTime.format(formatter));
        response.setEndTime(endTime.format(formatter));
        response.setTotalPrice(reservation.getTotalPrice());
        response.setStatus(reservation.getStatus());
        response.setSlotOpened(slot.isOpened());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> cancelReservation(
            @PathVariable long reservationId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        if (customUserDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = customUserDetails.getUser();

        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);

        if (reservation == null) {
            return ResponseEntity.status(403).body("해당 예약은 존재하지 않습니다.");
        }

        if (!reservation.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("해당 예약을 취소할 권한이 없습니다.");
        }

//        if (reservation.getStartTime().isBefore(java.time.LocalDateTime.now())) {
//            return ResponseEntity.badRequest().body("이미 시작된 예약은 취소할 수 없습니다.");
//        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        ParkingSlot slot = reservation.getParkingSlot();
        slot.setAvailable(true);
        slotRepository.save(slot);

        return ResponseEntity.ok("예약이 성공적으로 취소되었습니다.");
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyReservations(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false, defaultValue = "asc") String sort,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        if (customUserDetails == null) {
            return ResponseEntity.status(403).body("현재 로그인 상태가 아닙니다.");
        }

        User user = customUserDetails.getUser();
        List<Reservation> reservations;

        if (status != null) {
            reservations = reservationRepository.findByUserAndStatus(user, status);
        } else {
            reservations = reservationRepository.findByUser(user);
        }

        if(start != null && end != null) {
            reservations = reservations.stream()
                    .filter(r -> !r.getEndTime().isBefore(start) && !r.getStartTime().isAfter(end))
                    .collect(Collectors.toList());
        }

        if("desc".equalsIgnoreCase(sort)) {
            reservations.sort(Comparator.comparing(Reservation::getStartTime).reversed());
        } else {
            reservations.sort(Comparator.comparing(Reservation::getStartTime));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        List<ReservationResponseDTO> response = reservations.stream().map(
               reservation -> {
                   ReservationResponseDTO dto = new ReservationResponseDTO();
                   dto.setReservationId(reservation.getId());
                   dto.setUsername(user.getUsername());
                   dto.setSlotId(reservation.getParkingSlot().getId());
                   dto.setSlotNumber(reservation.getParkingSlot().getSlotNumber());
                   dto.setParkingLotName(reservation.getParkingSlot().getParkingLot().getName());
                   dto.setStartTime(reservation.getStartTime().format(formatter));
                   dto.setEndTime(reservation.getEndTime().format(formatter));
                   dto.setTotalPrice(reservation.getTotalPrice());
                   dto.setStatus(reservation.getStatus());
                   dto.setSlotOpened(reservation.getParkingSlot().isOpened());
                   return dto;
               }
        ).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllReservations(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        try {
            List<ReservationResponseDTO> result = reservationServiceImpl.getAllReservations(customUserDetails.getUser());
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("parking-lot/{spaceId}/statistics")
    public ResponseEntity<?> getParkingLotStatistics(
            @PathVariable Long spaceId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        try {
            ParkingLotStatisticsDTO dto = reservationServiceImpl.getParkingLotStatistics(customUserDetails.getUser(), spaceId);
            return ResponseEntity.ok(dto);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/parking-lot/{spaceId}")
    public ResponseEntity<?> getReservationsByParkingLotId(
            @PathVariable long spaceId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        try {
            List<ReservationResponseDTO> result = reservationServiceImpl.getReservationsByParkingLot(customUserDetails.getUser(), spaceId);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/admin/statistics")
    public ResponseEntity<?> getAllParkingLotStatistics(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        try {
            List<ParkingLotStatisticsDTO> result = reservationServiceImpl.getAllParkingLotStatistics(customUserDetails.getUser());
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private LocalDateTime parseLabelToTime(LocalDateTime baseDate, String label) {
        String[] parts = label.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return baseDate.withHour(hour).withMinute(minute);
    }

    @PatchMapping("/{reservationId}/status")
    public ResponseEntity<?> updateReservationStatus(
            @PathVariable long reservationId,
            @RequestParam ReservationStatus status,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        if (customUserDetails == null) {
            return ResponseEntity.status(401).body("로그인 필요");
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약이 존재하지 않습니다."));

        // 본인 예약인지 확인 (관리자 검사 없이 누구나 가능하게 하려면 이 조건도 제거 가능)
        if (!reservation.getUser().getId().equals(customUserDetails.getUser().getId())) {
            return ResponseEntity.status(403).body("본인의 예약만 상태 변경이 가능합니다.");
        }

        reservation.setStatus(status);
        reservationRepository.save(reservation);

        // 상태가 COMPLETED일 경우 ParkingSlot 상태도 갱신
        if (status == ReservationStatus.COMPLETED) {
            reservation.getParkingSlot().setOpened(false);     // 차단기 닫음
            reservation.getParkingSlot().setAvailable(true);   // 다시 예약 가능
        }

        return ResponseEntity.ok("예약 상태가 " + status + "로 변경되었습니다.");
    }
}
