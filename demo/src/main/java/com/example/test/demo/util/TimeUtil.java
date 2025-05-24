package com.example.test.demo.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimeUtil {
    /**
     * "HH:mm" 형식의 시간 슬롯을 LocalDateTime으로 변환
     * 예: baseDate = 2025-06-01, timeSlot = "09:00" → 2025-06-01T09:00
     */
    public static LocalDateTime parseTimeSlotToDateTime(LocalDate baseDate, String timeSlot) {
        String[] parts = timeSlot.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return baseDate.atTime(hour, minute);
    }
}
