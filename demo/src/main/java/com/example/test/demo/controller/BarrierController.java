package com.example.test.demo.controller;

import com.example.test.demo.config.CustomUserDetails;
import com.example.test.demo.entity.ParkingSlot;
import com.example.test.demo.entity.Reservation;
import com.example.test.demo.entity.ReservationStatus;
import com.example.test.demo.repository.ParkingSlotRepository;
import com.example.test.demo.repository.ReservationRepository;
import com.example.test.demo.service.MqttBarrierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/barrier")
public class BarrierController {

    private final ReservationRepository reservationRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final MqttBarrierService mqttBarrierService;

    @PostMapping("/{slotId}/open")
    public ResponseEntity<?> openBarrier(
            @PathVariable Long slotId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
        ) {
        Long userId = customUserDetails.getUser().getId();
        LocalDateTime now = LocalDateTime.now();

        Reservation reservation = reservationRepository.findActiveReservation(
            userId, slotId, now
        ).orElseThrow(
            () -> new RuntimeException("해당 슬롯에 활성 예약이 없습니다.")
        );

        // 슬롯 상태 변경
        ParkingSlot slot = reservation.getParkingSlot();
        slot.setOpened(true);
        parkingSlotRepository.save(slot);

        mqttBarrierService.sendCommand(slotId, "open");

        return ResponseEntity.ok(Map.of("message", "차단기 열림"));
    }

    @PostMapping("/{slotId}/close")
    public ResponseEntity<?> closeBarrier(
            @PathVariable Long slotId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Long userId = customUserDetails.getUser().getId();
        Reservation reservation = reservationRepository.findActiveReservation(
            userId, slotId, LocalDateTime.now()
        ).orElseThrow(
            () -> new RuntimeException("해당 슬롯에 활성 예약이 없습니다.")
        );

        // 예약 상태를 COMPLETED로 변경
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);

        // 슬롯 상태 변경
        ParkingSlot slot = reservation.getParkingSlot();
        slot.setAvailable(true);
        slot.setOpened(false);
        parkingSlotRepository.save(slot);

        mqttBarrierService.sendCommand(slotId, "close");
        return ResponseEntity.ok(
            Map.of("message", "차단기 닫힘 및 예약 완료 처리 완료")
        );
    }

    @PostMapping("/{slotId}/command")
    public ResponseEntity<?> sendBarrierCommand(
            @PathVariable Long slotId,
            @RequestBody Map<String, String> request
    ) {
        String command = request.get("command"); // "open" or "close"
        mqttBarrierService.sendCommand(slotId, command);
        return ResponseEntity.ok("명령 전송 완료: " + command);
    }
}
