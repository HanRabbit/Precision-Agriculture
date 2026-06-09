package han.hanstudio.precisionAgriculture.pest;

import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PestSystem {
    private static final Random RANDOM = new Random();
    private static final float SPREAD_CHANCE = 0.01f;
    private static final float SPAWN_CHANCE = 0.0005f;

    public static void tick(ServerWorld world) {
        SoilManager manager = SoilManager.get(world);
        List<BlockPos> infected = new ArrayList<>();

        manager.getAll().forEach((pos, soil) -> {
            if (soil.getPestType() != null) {
                infected.add(pos);
            } else if (RANDOM.nextFloat() < SPAWN_CHANCE) {
                PestType type = PestType.values()[RANDOM.nextInt(PestType.values().length)];
                soil.setPestType(type.name());
            }
        });

        // spread to neighbors
        for (BlockPos pos : infected) {
            for (BlockPos neighbor : getNeighbors(pos)) {
                SoilData neighborSoil = manager.get(neighbor);
                if (neighborSoil != null && neighborSoil.getPestType() == null
                        && RANDOM.nextFloat() < SPREAD_CHANCE) {
                    SoilData src = manager.get(pos);
                    neighborSoil.setPestType(src.getPestType());
                }
            }
        }
        if (!infected.isEmpty()) manager.markDirty();
    }

    public static boolean treatWithPesticide(SoilData soil, boolean isInsecticide) {
        if (soil.getPestType() == null) return false;
        PestType type = PestType.valueOf(soil.getPestType());
        boolean shouldCure = isInsecticide ? !type.isDisease : type.isDisease;
        if (shouldCure) { soil.setPestType(null); return true; }
        return false;
    }

    private static List<BlockPos> getNeighbors(BlockPos pos) {
        return List.of(pos.north(), pos.south(), pos.east(), pos.west());
    }
}
