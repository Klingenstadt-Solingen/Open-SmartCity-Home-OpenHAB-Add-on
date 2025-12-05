package org.openhab.binding.opensmartcityhome.internal.data;

import org.eclipse.jdt.annotation.Nullable;

import java.util.List;

public interface OpenSmartCityHomeApi {
    List<StationData> getAllStations();
    @Nullable StationData getStationFromId(String stationId);
    @Nullable String getStationIdFromSensorId(String sensorId);
}
