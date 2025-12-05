package org.openhab.binding.opensmartcityhome.internal.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfiguratorThingConfiguration {
    public List<String> selectedStationIds;

    public ConfiguratorThingConfiguration(Map<String, ?> map) {
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Boolean bool) {
                if (bool) {
                    list.add(key);
                }
            }
        }
        selectedStationIds = list;
    }
}
