package org.openhab.binding.opensmartcityhome.internal;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.opensmartcityhome.internal.data.StationData;
import org.openhab.core.config.core.*;
import org.openhab.core.thing.ThingTypeUID;
import org.osgi.service.component.annotations.Component;

@NonNullByDefault
@Component(service = ConfigDescriptionProvider.class)
public class ConfiguratorConfigDescriptionProvider implements ConfigDescriptionProvider {
    private static final ThingTypeUID SUPPORTED_THING_TYPE = Constants.THING_TYPE_CONFIGURATOR;
    @Override
    public @Nullable ConfigDescription getConfigDescription(URI uri, @Nullable Locale locale) {
        if (!uri.toString().contains(SUPPORTED_THING_TYPE.getId())) {
            return null;
        }

        List<StationData> stations = Constants.api.getAllStations();
        stations.sort(Comparator.comparing(StationData::getName));
        ArrayList<ConfigDescriptionParameter> parameters = new ArrayList<>();
        for (StationData station : stations) {
            parameters.add(
                    ConfigDescriptionParameterBuilder.create(station.id, ConfigDescriptionParameter.Type.BOOLEAN)
                            .withLabel(station.name)
                            .withGroupName("stationIds")
                            .build()
            );
        }
        ConfigDescriptionParameterGroup group = ConfigDescriptionParameterGroupBuilder.create("stationIds").withLabel("Ausgewählte Sensorstationen.").build();
        return ConfigDescriptionBuilder.create(uri)
                .withParameterGroup(group)
                .withParameters(parameters)
                .build();
    }

    @Override
    public List<ConfigDescription> getConfigDescriptions(@Nullable Locale locale) {
        return List.of();
    }
}
