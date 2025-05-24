package com.example.test.demo.dto;

import com.example.test.demo.entity.ParkingSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSlotDTO {
    private Long id;
    private int slotNumber;
    private boolean isAvailable;
    private boolean isOpened;

    public static ParkingSlotDTO from (ParkingSlot slot, boolean isAvailableForRequestedTime) {
        return ParkingSlotDTO.builder()
                .id(slot.getId())
                .slotNumber(slot.getSlotNumber())
                .isAvailable(isAvailableForRequestedTime)
                .isOpened(slot.isOpened())
                .build();
    }

}
