# Open SmartCity Home OpenHAB Binding

⚠️ **Beta Software**
This binding is currently in **beta**. Features, configuration options, and behavior may change. Use at your own risk and expect potential bugs.

## Overview

The **Open SmartCity Home OpenHAB Binding** integrates Open SmartCity Home sensor stations into OpenHAB.
It automatically creates Things for selected sensor stations and exposes their sensors as channels, allowing you to easily create Items and rules based on the data.

---

## Installation

### Manual Installation

1. Copy the binding JAR file into your OpenHAB add-ons directory:

   ```
   org.openhab.binding.opensmartcityhome-4.3.8.jar
   ```

   Typical location:

   ```
   $OPENHAB_HOME/addons
   ```

2. Restart OpenHAB.

---

## Verify Installation

1. Open the OpenHAB UI.
2. Go to **Settings → Add-ons → Bindings**.
3. Check if **OpenSmartCityHome Binding** is listed.
4. If it is not installed yet, install **OpenSmartCityHome Binding** from the Add-on Store.

---

## Configuration

### Add the Binding Thing

1. Go to **Settings → Things**.
2. Click **Add Thing**.
3. Select **Open SmartCity Binding**.
4. Choose **Open SmartCity Home Konfigurator**.
5. Create the Thing.

At this point, the Thing will be created **without any configuration**, and you will see an error indicating that no sensor stations are selected.

---

### Select Sensor Stations

1. Open the newly created **Open SmartCity Home Konfigurator** Thing.
2. Go to **Configuration**.
3. Select the **sensor stations** you want to add.
4. Save the configuration.

---

## Things & Channels

* Each selected **station** will be created as a **Thing**.
* Each sensor belonging to a station will be exposed as a **Channel**.
* Create **Items** for the channels as needed (manually or via semantic model).

---

## Notes

* This binding is still in **beta**.
* Error handling and configuration validation are limited.
* Feedback and contributions are welcome.

---

## License

This project is licensed under the [Open Smart City License](LICENSE.md).

## Legal notices and data reliability

Before using the data, please note the legal information [here](Legal-Notices-and-Data-Reliability.md).
