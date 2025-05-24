package com.example.test.demo.config;

import com.example.test.demo.service.MqttBarrierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/barrier")
public class BarrierController {
    private final MqttBarrierService mqttBarrierService;

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
