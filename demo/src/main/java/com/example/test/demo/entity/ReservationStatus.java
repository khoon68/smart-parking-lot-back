package com.example.test.demo.entity;

public enum ReservationStatus {
    RESERVED, // 예약은 되어있지만 사용 전 상태
    ACTIVE, // 사용 상태
    CANCELLED, // 취소 상태
    COMPLETED // 사용 완료 상태
}
