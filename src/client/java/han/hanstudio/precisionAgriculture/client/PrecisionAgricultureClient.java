package han.hanstudio.precisionAgriculture.client;

import han.hanstudio.precisionAgriculture.client.screen.*;
import han.hanstudio.precisionAgriculture.network.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PrecisionAgricultureClient implements ClientModInitializer {

    private static final List<BlockPos> infectedPositions = new CopyOnWriteArrayList<>();
    private static final List<BlockPos> irrigatorPositions = new CopyOnWriteArrayList<>();
    private int particleTick = 0;

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

        ClientPlayNetworking.registerGlobalReceiver(OpenIrrigatorPayload.ID,
                (payload, ctx) -> ctx.client().execute(() ->
                        ctx.client().setScreen(new IrrigatorScreen(payload))));

        ClientPlayNetworking.registerGlobalReceiver(OpenPlanterPayload.ID,
                (payload, ctx) -> ctx.client().execute(() ->
                        ctx.client().setScreen(new PlanterScreen(payload))));

        ClientPlayNetworking.registerGlobalReceiver(OpenHarvesterPayload.ID,
                (payload, ctx) -> ctx.client().execute(() ->
                        ctx.client().setScreen(new HarvesterScreen(payload))));

        ClientPlayNetworking.registerGlobalReceiver(OpenPesticideSprayerPayload.ID,
                (payload, ctx) -> ctx.client().execute(() ->
                        ctx.client().setScreen(new PesticideSprayerScreen(payload))));

        ClientPlayNetworking.registerGlobalReceiver(SyncAgriTerminalPayload.ID, (payload, ctx) -> ctx.client().execute(() -> {
            if (ctx.client().currentScreen instanceof AgriTerminalScreen scr && scr.pos().equals(payload.terminalPos()))
                scr.update(payload.totalPlots(), payload.avgMoisture(), payload.avgFertility(),
                        payload.avgHealth(), payload.pestRate(), payload.advice());
        }));

        ClientPlayNetworking.registerGlobalReceiver(SyncMachineStatusPayload.ID, (payload, ctx) -> ctx.client().execute(() -> {
            if (payload.isHarvester() && ctx.client().currentScreen instanceof HarvesterScreen hs && hs.pos().equals(payload.pos()))
                hs.updateStatus(payload.range(), payload.count());
            else if (!payload.isHarvester() && ctx.client().currentScreen instanceof PlanterScreen ps && ps.pos().equals(payload.pos()))
                ps.updateStatus(payload.range(), payload.count());
        }));

        ClientPlayNetworking.registerGlobalReceiver(SyncPesticideSprayerPayload.ID, (payload, ctx) -> ctx.client().execute(() -> {
            if (ctx.client().currentScreen instanceof PesticideSprayerScreen scr && scr.pos().equals(payload.pos()))
                scr.updateStatus(payload.range(), payload.sprayedLast(), payload.slot0(), payload.slot1());
        }));

        ClientPlayNetworking.registerGlobalReceiver(SyncInfectedPayload.ID,
                (payload, ctx) -> ctx.client().execute(() -> {
                    infectedPositions.clear();
                    infectedPositions.addAll(payload.positions());
                }));

        ClientPlayNetworking.registerGlobalReceiver(SyncIrrigatorPayload.ID, (payload, ctx) -> ctx.client().execute(() -> {
            irrigatorPositions.clear();
            irrigatorPositions.addAll(payload.positions());
        }));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (++particleTick % 10 != 0) return;
            if (client.world == null || client.player == null) return;
            for (BlockPos soil : infectedPositions) {
                BlockPos crop = soil.up();
                if (!client.world.isChunkLoaded(crop.getX() >> 4, crop.getZ() >> 4)) continue;
                double x = crop.getX() + 0.3 + client.world.random.nextDouble() * 0.4;
                double y = crop.getY() + 0.5 + client.world.random.nextDouble() * 0.3;
                double z = crop.getZ() + 0.3 + client.world.random.nextDouble() * 0.4;
                client.world.addParticleClient(ParticleTypes.SMOKE, x, y, z, 0.0, 0.02, 0.0);
            }
            for (BlockPos irrigator : irrigatorPositions) {
                if (!client.world.isChunkLoaded(irrigator.getX() >> 4, irrigator.getZ() >> 4)) continue;
                double x = irrigator.getX() + 0.2 + client.world.random.nextDouble() * 0.6;
                double y = irrigator.getY() + 0.1;
                double z = irrigator.getZ() + 0.2 + client.world.random.nextDouble() * 0.6;
                client.world.addParticleClient(ParticleTypes.DRIPPING_WATER, x, y, z, 0.0, 0.0, 0.0);
            }
        });
    }
}
