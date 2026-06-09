package han.hanstudio.precisionAgriculture.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ModNetworking {
    public static void registerServerPayloads() {
        PayloadTypeRegistry.playS2C().register(OpenSoilSensorPayload.ID, OpenSoilSensorPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenWeatherStationPayload.ID, OpenWeatherStationPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenAgriTerminalPayload.ID, OpenAgriTerminalPayload.CODEC);
    }
}
