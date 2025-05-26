package com.example.test.demo.controller;

import com.example.test.demo.config.CustomUserDetails;
import com.example.test.demo.entity.Reservation;
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
    private final MqttBarrierService mqttBarrierService;

    @PostMapping("/{slotId}/open")
    public ResponseEntity<?> openBarrier(
            @PathVariable Long slotId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
        ) {
        Long userId = customUserDetails.getUser().getId();
        LocalDateTime now = LocalDateTime.now();

        Reservation reservation = reservationRepository.findActiveReservationNow(userId, slotId, now)
                .orElseThrow(() -> new RuntimeException("현재 시간에 해당 유저의 유효한 예약이 없습니다."));

        mqttBarrierService.sendCommand(slotId, "open");
        return ResponseEntity.ok("차단기 열림 요청 성공");
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
