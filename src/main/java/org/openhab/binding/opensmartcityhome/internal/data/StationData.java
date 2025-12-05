package org.openhab.binding.opensmartcityhome.internal.data;

import org.eclipse.jdt.annotation.Nullable;

import java.util.List;

public class StationData {
    public String id;
    public String name;
    public List<SensorData> sensors;

    public StationData(String id, String name, List<SensorData> sensors) {
        this.id = id;
        this.name = name;
        this.sensors = sensors;
    }

    public @Nullable SensorData getSensorFromId(String sensorId) {
        for (SensorData sensor : sensors) {
            if (sensorId.equals(sensor.id)) {
                return sensor;
            }
        }
        return null;
    }
}
