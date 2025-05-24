package com.example.test.demo.repository;

import com.example.test.demo.entity.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {

    List<ParkingLot> findByAvailableSlotsGreaterThan(int minAvailable);
    Optional<ParkingLot> findByName(String name); // 주차장 이름으로 검색 기능
}
