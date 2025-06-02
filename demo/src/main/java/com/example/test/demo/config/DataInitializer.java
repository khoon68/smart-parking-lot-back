package com.example.test.demo.config;

import com.example.test.demo.entity.*;
import com.example.test.demo.repository.ParkingSlotRepository;
import com.example.test.demo.repository.ParkingLotRepository;
import com.example.test.demo.repository.ReservationRepository;
import com.example.test.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository UserRepository;
    private final ParkingLotRepository ParkingLotRepository;
    private final ParkingSlotRepository ParkingSlotRepository;
    private final ReservationRepository ReservationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (UserRepository.count() == 0) {
            // 1. 사용자 2명 생성 (ID는 자동 생성)
            User user1 = UserRepository.save(
                    User.builder()
                            .username("user1")
                            .password(passwordEncoder.encode("1234"))
                            .email("user1@example.com")
                            .phone("010-0000-1111")
                            .role(UserRole.USER)
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            User user2 = UserRepository.save(
                    User.builder()
                            .username("user2")
                            .password(passwordEncoder.encode("1234"))
                            .email("user2@example.com")
                            .phone("010-0000-2222")
                            .role(UserRole.USER)
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            User admin = UserRepository.save(
                    User.builder()
                            .username("admin")
                            .password(passwordEncoder.encode("admin"))
                            .email("admin@example.com")
                            .phone("010-9999-9999")
                            .role(UserRole.ADMIN)
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            // 2. user1이 주차공간 1개 생성
            ParkingLot space1 = ParkingLotRepository.save(
                    ParkingLot.builder()
                            .name("테스트 주차장1")
                            .distance(100)
                            .owner(user1)
                            .pricePerHour(1000)
                            .totalSlots(6)
                            .availableSlots(4)
                            .build()
            );

            ParkingLot space2 = ParkingLotRepository.save(
                    ParkingLot.builder()
                            .name("테스트 주차장2")
                            .distance(200)
                            .owner(user2)
                            .pricePerHour(2000)
                            .totalSlots(6)
                            .availableSlots(4)
                            .build()
            );

            // 3. 주차공간에 슬롯 6개 생성
            ParkingSlot s1 = ParkingSlot.builder().slotNumber(1).isAvailable(true).isOpened(false).hardwareId("esp01").parkingLot(space1).build();
            ParkingSlot s2 = ParkingSlot.builder().slotNumber(2).isAvailable(true).isOpened(false).hardwareId("esp02").parkingLot(space1).build();
            ParkingSlot s3 = ParkingSlot.builder().slotNumber(3).isAvailable(true).isOpened(false).hardwareId("esp03").parkingLot(space1).build();
            ParkingSlot s4 = ParkingSlot.builder().slotNumber(4).isAvailable(true).isOpened(false).hardwareId("25").parkingLot(space1).build();
            ParkingSlot s5 = ParkingSlot.builder().slotNumber(5).isAvailable(true).isOpened(false).hardwareId("26").parkingLot(space1).build();
            ParkingSlot s6 = ParkingSlot.builder().slotNumber(6).isAvailable(true).isOpened(false).hardwareId("27").parkingLot(space1).build();
            ParkingSlotRepository.saveAll(List.of(s1, s2, s3, s4, s5, s6));

            ParkingSlot s7 = ParkingSlot.builder().slotNumber(1).isAvailable(true).isOpened(false).hardwareId("esp07").parkingLot(space2).build();
            ParkingSlot s8 = ParkingSlot.builder().slotNumber(2).isAvailable(true).isOpened(false).hardwareId("esp08").parkingLot(space2).build();
            ParkingSlot s9 = ParkingSlot.builder().slotNumber(3).isAvailable(true).isOpened(false).hardwareId("esp09").parkingLot(space2).build();
            ParkingSlot s10 = ParkingSlot.builder().slotNumber(4).isAvailable(true).isOpened(false).hardwareId("esp10").parkingLot(space2).build();
            ParkingSlot s11 = ParkingSlot.builder().slotNumber(5).isAvailable(true).isOpened(false).hardwareId("esp11").parkingLot(space2).build();
            ParkingSlot s12 = ParkingSlot.builder().slotNumber(6).isAvailable(true).isOpened(false).hardwareId("esp12").parkingLot(space2).build();
            ParkingSlotRepository.saveAll(List.of(s7, s8, s9, s10, s11, s12));
        }
    }
}
