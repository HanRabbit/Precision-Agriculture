package han.hanstudio.precisionAgriculture;

import han.hanstudio.precisionAgriculture.crop.CropGrowthHandler;
import han.hanstudio.precisionAgriculture.network.ModNetworking;
import han.hanstudio.precisionAgriculture.network.SyncInfectedPayload;
import han.hanstudio.precisionAgriculture.network.SyncIrrigatorPayload;
import han.hanstudio.precisionAgriculture.pest.PestSystem;
import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.entity.BlockEntity;
import han.hanstudio.precisionAgriculture.block.entity.IrrigatorBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class PrecisionAgriculture implements ModInitializer {

    @Override
    public void onInitialize() {
        ModNetworking.registerServerPayloads();
        ModRegistries.register();
        CropGrowthHandler.register();
        registerSoilTracking();
        registerPestTick();
        registerInfectedCropDropSuppression();
    }

    private void registerSoilTracking() {
        // Create SoilData when a player interacts with farmland
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (world.isClient()) return ActionResult.PASS;
            if (world.getBlockState(hit.getBlockPos()).getBlock() instanceof FarmlandBlock) {
                SoilManager.get((ServerWorld) world).getOrCreate(hit.getBlockPos());
            }
            return ActionResult.PASS;
        });

        // Tick all soil data every world tick
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            SoilManager.get(world).tickAll(world.isRaining());
        });
    }

    private void registerInfectedCropDropSuppression() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
            if (!world.isClient() && world.getBlockEntity(pos) instanceof han.hanstudio.precisionAgriculture.block.entity.PesticideSprayerBlockEntity be)
                be.dropContents();
            if (!(state.getBlock() instanceof CropBlock crop)) return true;
            SoilData soil = SoilManager.get((ServerWorld) world).get(pos.down());
            if (soil == null) return true;
            if (crop.isMature(state)) {
                soil.setFertility(soil.getFertility() - 15f);
                SoilManager.get((ServerWorld) world).markDirty();
            }
            if (soil.getPestType() != null && !player.isCreative()) {
                world.breakBlock(pos, false, player);
                return false;
            }
            return true;
        });
    }

    private void registerPestTick() {
        final int[] tick = {0};
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            int t = ++tick[0];
            if (t % 100 == 0) PestSystem.tick(world);
            if (t % 20 == 0) {
                broadcastInfected(world);
                broadcastIrrigators(world);
            }
        });
    }

    private void broadcastInfected(ServerWorld world) {
        List<BlockPos> infected = new java.util.ArrayList<>();
        SoilManager.get(world).getAll().forEach((pos, soil) -> {
            if (soil.getPestType() == null) return;
            if (!(world.getBlockState(pos.up()).getBlock() instanceof CropBlock)) {
                soil.setPestType(null);
                return;
            }
            infected.add(pos);
        });
        world.getPlayers().forEach(p -> ServerPlayNetworking.send(p, new SyncInfectedPayload(infected)));
    }

    private void broadcastIrrigators(ServerWorld world) {
        List<BlockPos> irrigators = new java.util.ArrayList<>();
        irrigators.addAll(IrrigatorBlockEntity.activePositions);
        world.getPlayers().forEach(p -> ServerPlayNetworking.send(p, new SyncIrrigatorPayload(irrigators)));
    }
}
