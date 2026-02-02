/*
 * Copyright (c) 2010-2025 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.opensmartcityhome.internal;

import static org.openhab.binding.opensmartcityhome.internal.Constants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.opensmartcityhome.internal.data.ConfiguratorThingConfiguration;
import org.openhab.binding.opensmartcityhome.internal.data.EmptyConfiguration;
import org.openhab.core.thing.*;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The {@link ConfiguratorHomeHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Silvester Nita - Initial contribution
 */
@NonNullByDefault
public class ConfiguratorHomeHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(ConfiguratorHomeHandler.class);

    private @Nullable MqttBridge mqttBridge = null;

    public @Nullable ConfiguratorThingConfiguration config;

    public ConfiguratorHomeHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void initialize() {
        Map<String, ?> map = getConfig().getProperties();

        config = new ConfiguratorThingConfiguration(map);

        if (config.selectedStationIds == null || config.selectedStationIds.isEmpty()) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Keine Stationen ausgewählt!");
            return;
        }
        logger.debug("Configured stations: {}", config.selectedStationIds);

        scheduler.execute(() -> {
            BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
            ServiceReference<ThingRegistry> reference = context.getServiceReference(ThingRegistry.class);
            if (reference != null) {
                ThingRegistry registry = context.getService(reference);
                for (String stationId : config.selectedStationIds) {
                    ThingUID stationUID = new ThingUID(BINDING_ID, STATION_THING_TYPE_KEY, stationId);
                    Thing existing = registry.get(stationUID);
                    if (existing == null) {
                        Thing newThing = registry.createThingOfType(THING_TYPE_STATION, stationUID, null, stationId, new EmptyConfiguration());
                        if (newThing != null) {
                            registry.add(newThing);
                        }
                    }
                }
                Collection<Thing> allThings = registry.getAll();
                for (Thing t : allThings) {
                    if (t.getThingTypeUID().equals(THING_TYPE_STATION)) {
                        String stationId = t.getUID().getId();
                        if (!config.selectedStationIds.contains(stationId)) {
                            registry.remove(t.getUID());
                        }
                    }
                }
                updateStatus(ThingStatus.ONLINE);
                if (mqttBridge != null) {
                    logger.info("Stopping old mqtt client.");
                    mqttBridge.stop();
                }
                logger.info("Info trying to start new mqtt client");
                mqttBridge = new MqttBridge(this);
                mqttBridge.start();
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "Could not initialize stations.");
            }
        });
    }

    @Override
    public void handleRemoval() {
        BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
        ServiceReference<ThingRegistry> reference = context.getServiceReference(ThingRegistry.class);
        if (reference != null) {
            ThingRegistry registry = context.getService(reference);
            Collection<Thing> allThings = registry.getAll();
            for (Thing t : allThings) {
                if (t.getThingTypeUID().equals(THING_TYPE_STATION)) {
                    registry.remove(t.getUID());
                }
            }
        }
        if (mqttBridge != null) {
            mqttBridge.stop();
            mqttBridge = null;
        }
        super.handleRemoval();
    }

    public void updateSensorState(ChannelUID channelUID, State state) {
        updateState(channelUID, state);
    }

    public void updateStationStatus(ThingUID thingUID, String status) {
        BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
        ServiceReference<ThingRegistry> reference = context.getServiceReference(ThingRegistry.class);
        if (reference != null) {
            ThingRegistry registry = context.getService(reference);
            Thing station = registry.get(thingUID);
            if (station != null) {
                ThingHandler handler = station.getHandler();
                if (handler instanceof StationHandler) {
                    ((StationHandler) handler).updateStationStatus(status);
                }
            }
        }
    }
}
