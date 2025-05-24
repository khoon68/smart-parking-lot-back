package com.example.test.demo.repository;

import com.example.test.demo.entity.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    // 특정 주차공간 내 자리 조회
    List<ParkingSlot> findByParkingLotId(Long parkingLotId);

    // 사용 가능한 자리만
    List<ParkingSlot> findByParkingLotIdAndIsAvailableTrue(Long parkingLotId);


}
