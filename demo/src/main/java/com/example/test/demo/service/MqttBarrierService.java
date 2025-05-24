package com.example.test.demo.service;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Service
public class MqttBarrierService {
    private final String broker = "tcp://localhost:1883"; // 또는 EC2 IP
    private final String topicPrefix = "barrier/";

    public void sendCommand(Long slotId, String command) {
        String topic = topicPrefix + slotId + "/cmd";

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
