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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.opensmartcityhome.internal.data.OpenSmartCityHomeApi;
import org.openhab.binding.opensmartcityhome.internal.data.OpenSmartCityHomeApiImpl;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.type.ChannelTypeUID;

@NonNullByDefault
public class Constants {

    public static final String BINDING_ID = "opensmartcityhome";
    public static final String CONFIGURATOR_THING_TYPE_KEY = "configurator";
    public static final String STATION_THING_TYPE_KEY = "station";
    public static final String SENSOR_CHANNEL_TYPE_KEY = "sensor";

    public static final ThingTypeUID THING_TYPE_CONFIGURATOR = new ThingTypeUID(BINDING_ID, CONFIGURATOR_THING_TYPE_KEY);
    public static final ThingTypeUID THING_TYPE_STATION = new ThingTypeUID(BINDING_ID, STATION_THING_TYPE_KEY);
    public static final ChannelTypeUID CHANNEL_TYPE_SENSOR = new ChannelTypeUID(BINDING_ID, SENSOR_CHANNEL_TYPE_KEY);

    public static final OpenSmartCityHomeApi api = new OpenSmartCityHomeApiImpl();

    public static final String MQTT_URI = "tcp://159.69.38.127:1883";
    public static final String MQTT_USERNAME = "demo-user";
    public static final String MQTT_PASSWORD = "demo-password";
    public static final String MQTT_STATE_TOPIC_TYPE = "sensor";
    public static final String MQTT_STATE_TOPIC = "opensmartcityhome/" + MQTT_STATE_TOPIC_TYPE + "/+/state";
    public static final String MQTT_STATUS_TOPIC_TYPE = "station";
    public static final String MQTT_STATUS_TOPIC = "opensmartcityhome/" + MQTT_STATUS_TOPIC_TYPE + "/+/status";
    public static final String API_URL = "http://159.69.38.127:8888/stations";
    public static final String API_USER = "client";
    public static final String API_PASSWORD = "client-password";
}
