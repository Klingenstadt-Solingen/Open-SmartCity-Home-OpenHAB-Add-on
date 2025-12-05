package org.openhab.binding.opensmartcityhome.internal;

import org.openhab.binding.opensmartcityhome.internal.data.SensorData;
import org.openhab.binding.opensmartcityhome.internal.data.StationData;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.thing.*;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.thing.binding.builder.ChannelBuilder;
import org.openhab.core.thing.binding.builder.ThingBuilder;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openhab.binding.opensmartcityhome.internal.Constants.CHANNEL_TYPE_SENSOR;

public class StationHandler extends BaseThingHandler {
    private final Logger logger = LoggerFactory.getLogger(StationHandler.class);
    public StationHandler(Thing thing) {
        super(thing);
    }
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void initialize() {
        String stationId = getThing().getUID().getId();
        StationData station = Constants.api.getStationFromId(stationId);
        if (station != null) {
            logger.info("Station {} config loaded", stationId);
            ThingBuilder thingBuilder = editThing();
            thingBuilder.withLabel(station.name);
            for (SensorData sensor : station.sensors) {
                ChannelUID channelUID = new ChannelUID(getThing().getUID(), sensor.id);
                Channel channel = null;
                for (Channel thingChannel : getThing().getChannels()) {
                    if (channelUID.equals(thingChannel.getUID())) {
                        channel = thingChannel;
                        break;
                    }
                }
                if (channel == null) {
                    channel = ChannelBuilder.create(channelUID, "Number")
                            .withType(CHANNEL_TYPE_SENSOR)
                            .withLabel(sensor.name)
                            .build();
                    thingBuilder.withChannel(channel);
                }
                if (sensor.state != null) {
                    updateState(channelUID, new DecimalType(sensor.state));
                }
            }
            updateThing(thingBuilder.build());

            updateStatus(ThingStatus.ONLINE);
        } else {
            logger.error("Station {} config could not be found!", stationId);
            updateStatus(ThingStatus.OFFLINE);
        }
    }
}

