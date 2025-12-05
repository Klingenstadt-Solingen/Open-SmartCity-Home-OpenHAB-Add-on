package org.openhab.binding.opensmartcityhome.internal;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.openhab.binding.opensmartcityhome.internal.Constants.*;

public class MqttBridge {
    private final Logger logger = LoggerFactory.getLogger(MqttBridge.class);
    private MqttClient client;
    private ConfiguratorHomeHandler configuratorHomeHandler;

    public MqttBridge(ConfiguratorHomeHandler configuratorHomeHandler) {
        this.configuratorHomeHandler = configuratorHomeHandler;
    }

    public void start() {
        logger.debug("Starting MQTT connection.");
        try {
            String clientId = UUID.randomUUID().toString();

            client = new MqttClient(MQTT_URI, clientId);
            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setCleanStart(true);

            options.setUserName(MQTT_USERNAME);
            options.setPassword(MQTT_PASSWORD.getBytes(StandardCharsets.UTF_8));

            client.connect(options);

            MqttSubscription[] subscriptions = new MqttSubscription[]{
                    new MqttSubscription(MQTT_TOPIC, 1)
            };

            IMqttMessageListener listener = (topic, mqttMessage) -> {
                String payload = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
                logger.info("Received MQTT message: topic={} payload={}", topic, payload);
                handleMessage(topic, payload);
            };
            IMqttMessageListener[] messageListeners = new IMqttMessageListener[]{ listener };
            client.subscribe(subscriptions, messageListeners);

            logger.info("MQTT subscribed to topic {}", MQTT_TOPIC);

        } catch (MqttException e) {
            logger.error("Error connecting/subscribing MQTT: {}", e.getMessage(), e);
        }
    }

    public void stop() {
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
                logger.info("MQTT disconnected.");
            } catch (MqttException e) {
                logger.warn("Error disconnecting MQTT: {}", e.getMessage(), e);
            }
        }
    }

    private void handleMessage(String topic, String message) {
        String sensorId = null;
        String[] parts = topic.split("/");
        if (parts.length == 4) {
            sensorId = parts[2];
        }
        Double value = null;
        try {
            value = Double.parseDouble(message);
        } catch (Exception ignored) {}
        if (sensorId != null && value != null) {
            String stationId = api.getStationIdFromSensorId(sensorId);
            if (stationId != null && configuratorHomeHandler != null && configuratorHomeHandler.config != null) {
                if (configuratorHomeHandler.config.selectedStationIds.contains(stationId)) {
                    ThingUID stationUID = new ThingUID(BINDING_ID, STATION_THING_TYPE_KEY, stationId);
                    ChannelUID channelUID = new ChannelUID(stationUID, sensorId);
                    configuratorHomeHandler.updateSensorState(channelUID, new DecimalType(value));
                }
            }
        }
    }
}
