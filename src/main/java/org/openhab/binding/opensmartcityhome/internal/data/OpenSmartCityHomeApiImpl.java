package org.openhab.binding.opensmartcityhome.internal.data;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.opensmartcityhome.internal.ConfiguratorHomeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import static org.openhab.binding.opensmartcityhome.internal.Constants.*;

public class OpenSmartCityHomeApiImpl implements OpenSmartCityHomeApi {
    private final Logger logger = LoggerFactory.getLogger(OpenSmartCityHomeApiImpl.class);
    private final long oneHourInMillis = 60L * 60L * 1000L;
    private @Nullable Date cacheDate;
    private @Nullable List<StationData> cachedStations;

    private List<StationData> fetchStations() {
        try {
            logger.info("Fetching stations from api.");
            HttpClient client = HttpClient.newHttpClient();
            String credentials = API_USER + ":" + API_PASSWORD;
            String encodedAuth = Base64.getEncoder().encodeToString(credentials.getBytes());
            String authHeader = "Basic " + encodedAuth;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .header("Accept", "application/json")
                    .header("Authorization", authHeader)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String json = response.body();
                ObjectMapper mapper = new ObjectMapper();
                List<StationData> stations = new ArrayList<>();
                JsonNode root = mapper.readTree(json);
                for (JsonNode node : root) {
                    try {
                        String stationId = node.get("id").asText();
                        String stationName = node.get("name").asText();

                        if (stationId != null && stationName != null) {
                            List<SensorData> sensors = new ArrayList<>();
                            for (JsonNode sensorNode : node.get("sensors")) {
                                try {
                                    String sensorId = sensorNode.get("id").asText();
                                    String sensorName = sensorNode.get("name").asText();
                                    String sensorUnit = sensorNode.get("unit").asText();
                                    Double sensorState = sensorNode.get("state").asDouble();

                                    sensors.add(new SensorData(sensorId, sensorName, sensorUnit, sensorState));
                                } catch (Exception e) {
                                    logger.error("Failed to parse sensor data!", e);
                                }
                            }
                            stations.add(new StationData(stationId, stationName, sensors));
                        }
                    } catch (Exception e) {
                        logger.error("Failed to parse station data!", e);
                    }
                }
                return stations;
            }
        } catch (Exception e) {
            logger.error("Failed to fetch stations!", e);
        }
        return List.of();
    }

    @Override
    public List<StationData> getAllStations() {
        if (cacheDate != null) {
            long cacheTime = cacheDate.getTime();
            long nowDateTime = new Date().getTime();
            if (nowDateTime - cacheTime > oneHourInMillis) {
                cacheDate = null;
            }
        }

        if (cacheDate != null && cachedStations != null) {
            return cachedStations;
        } else {
            List<StationData> fetchedStations = fetchStations();
            cachedStations = fetchedStations;
            cacheDate = new Date();
            return fetchedStations;
        }
    }

    @Override
    public @Nullable StationData getStationFromId(String stationId) {
        for (StationData station : getAllStations()) {
            if (stationId.equals(station.id)) {
                return station;
            }
        }
        return null;
    }

    @Override
    public @Nullable String getStationIdFromSensorId(String sensorId) {
        for (StationData station : getAllStations()) {
            for (SensorData sensor : station.sensors) {
                if (sensor.id.equals(sensorId)) {
                    return station.id;
                }
            }
        }
        return null;
    }
}
