package com.example.test.demo.scheduler;

import com.example.test.demo.entity.Reservation;
import com.example.test.demo.entity.ReservationStatus;
import com.example.test.demo.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;

    @Scheduled(fixedRate = 60000) // 30초마다 실행
    public void completeExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> expiredReservations = reservationRepository.findByStatusAndEndTimeBefore(ReservationStatus.ACTIVE, now);

        for (Reservation reservation: expiredReservations) {
            reservation.setStatus(ReservationStatus.COMPLETED);
        }

        reservationRepository.saveAll(expiredReservations);

        if (!expiredReservations.isEmpty()) {
            log.info("자동으로 완료 처리된 예약 수: {}", expiredReservations.size());
        }
    }

    @Scheduled(fixedRate = 60000) // 30초마다 실행
    public void activateReservations() {
        LocalDateTime now = LocalDateTime.now();

        // 예약 상태가 RESERVED이고 현재 시간의 이후인 예약만을 조회
        List<Reservation> toActivate = reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.RESERVED)
                .filter(r -> !r.getStartTime().isAfter(now))
                .toList();

        for (Reservation reservation: toActivate) {
            reservation.setStatus(ReservationStatus.ACTIVE);
            log.info("[예약 활성화] id={}, 시간={} -> ACTIVE", reservation.getId(), reservation.getStartTime());
        }
        reservationRepository.saveAll(toActivate);
    }
}
