package com.example.test.demo.service;

import com.example.test.demo.entity.ParkingSlot;
import com.example.test.demo.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MqttBarrierService {
    private final ParkingSlotRepository slotRepository;
    private final String broker = "tcp://localhost:1883"; // 또는 EC2 IP
    private final String topicPrefix = "barrier/";

    public void sendCommand(Long slotId, String command) {
        ParkingSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("해당 슬롯을 찾을 수 없습니다."));
        String hardwareId = slot.getHardwareId();
        String topic = topicPrefix + hardwareId + "/cmd";

        try {
            MqttClient client = new MqttClient(broker, MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client.connect(options);

            MqttMessage message = new MqttMessage(command.getBytes());
            message.setQos(1);
            client.publish(topic, message);

            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
            throw new RuntimeException("MQTT 메시지 전송 실패");
        }
    }
}
