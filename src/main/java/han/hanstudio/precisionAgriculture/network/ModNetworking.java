package han.hanstudio.precisionAgriculture.network;

import han.hanstudio.precisionAgriculture.block.entity.HarvesterBlockEntity;
import han.hanstudio.precisionAgriculture.block.entity.IrrigatorBlockEntity;
import han.hanstudio.precisionAgriculture.block.entity.PesticideSprayerBlockEntity;
import han.hanstudio.precisionAgriculture.block.entity.PlanterBlockEntity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class ModNetworking {
    public static void registerServerPayloads() {
        PayloadTypeRegistry.playS2C().register(OpenSoilSensorPayload.ID, OpenSoilSensorPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenWeatherStationPayload.ID, OpenWeatherStationPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenAgriTerminalPayload.ID, OpenAgriTerminalPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncAgriTerminalPayload.ID, SyncAgriTerminalPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncInfectedPayload.ID, SyncInfectedPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncIrrigatorPayload.ID, SyncIrrigatorPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenIrrigatorPayload.ID, OpenIrrigatorPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenPlanterPayload.ID, OpenPlanterPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenHarvesterPayload.ID, OpenHarvesterPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncMachineStatusPayload.ID, SyncMachineStatusPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenPesticideSprayerPayload.ID, OpenPesticideSprayerPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncPesticideSprayerPayload.ID, SyncPesticideSprayerPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SetIrrigatorRangePayload.ID, SetIrrigatorRangePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SetMachineRangePayload.ID, SetMachineRangePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SetPesticideSprayerRangePayload.ID, SetPesticideSprayerRangePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PesticideSprayerSlotClickPayload.ID, PesticideSprayerSlotClickPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SetIrrigatorRangePayload.ID, (payload, ctx) -> {
            BlockPos pos = payload.pos();
            ctx.server().execute(() -> {
                ServerWorld world = ctx.player().getEntityWorld() instanceof ServerWorld sw ? sw : null;
                if (world != null && world.getBlockEntity(pos) instanceof IrrigatorBlockEntity be)
                    be.setRange(payload.range());
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SetMachineRangePayload.ID, (payload, ctx) -> {
            BlockPos pos = payload.pos();
            ctx.server().execute(() -> {
                ServerWorld world = ctx.player().getEntityWorld() instanceof ServerWorld sw ? sw : null;
                if (world == null) return;
                BlockEntity be = world.getBlockEntity(pos);
                if (payload.isHarvester() && be instanceof HarvesterBlockEntity h) h.setRange(payload.range());
                else if (!payload.isHarvester() && be instanceof PlanterBlockEntity p) p.setRange(payload.range());
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SetPesticideSprayerRangePayload.ID, (payload, ctx) -> {
            BlockPos pos = payload.pos();
            ctx.server().execute(() -> {
                ServerWorld world = ctx.player().getEntityWorld() instanceof ServerWorld sw ? sw : null;
                if (world != null && world.getBlockEntity(pos) instanceof PesticideSprayerBlockEntity be)
                    be.setRange(payload.range());
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(PesticideSprayerSlotClickPayload.ID, (payload, ctx) -> {
            BlockPos pos = payload.pos();
            ctx.server().execute(() -> {
                ServerWorld world = ctx.player().getEntityWorld() instanceof ServerWorld sw ? sw : null;
                if (world == null) return;
                if (ctx.player().getBlockPos().getSquaredDistance(pos) > 64 * 64) return;
                if (world.getBlockEntity(pos) instanceof PesticideSprayerBlockEntity be)
                    be.handleSlotClick(ctx.player(), payload.slot(), payload.button());
            });
        });
    }
}
