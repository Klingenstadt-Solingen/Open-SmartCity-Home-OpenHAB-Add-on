package org.openhab.binding.opensmartcityhome.internal.data;

public class SensorData {
    public String id;
    public String name;
    public String unit;
    public Double state;

    public SensorData(String id, String name, String unit, Double state) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.state = state;
    }
}
