package han.hanstudio.precisionAgriculture.client;

import han.hanstudio.precisionAgriculture.client.screen.AgriTerminalScreen;
import han.hanstudio.precisionAgriculture.client.screen.SoilSensorScreen;
import han.hanstudio.precisionAgriculture.client.screen.WeatherStationScreen;
import han.hanstudio.precisionAgriculture.network.OpenAgriTerminalPayload;
import han.hanstudio.precisionAgriculture.network.OpenSoilSensorPayload;
import han.hanstudio.precisionAgriculture.network.OpenWeatherStationPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class PrecisionAgricultureClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(OpenSoilSensorPayload.ID,
                (payload, ctx) -> ctx.client().execute(() ->
                        ctx.client().setScreen(new SoilSensorScreen(payload))));

        ClientPlayNetworking.registerGlobalReceiver(OpenWeatherStationPayload.ID,
                (payload, ctx) -> ctx.client().execute(() ->
                        ctx.client().setScreen(new WeatherStationScreen(payload))));

        ClientPlayNetworking.registerGlobalReceiver(OpenAgriTerminalPayload.ID,
                (payload, ctx) -> ctx.client().execute(() ->
                        ctx.client().setScreen(new AgriTerminalScreen(payload))));
    }
}
