package han.hanstudio.precisionAgriculture.farm;

import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class FarmStats {
    public final int totalPlots;
    public final float avgMoisture;
    public final float avgFertility;
    public final float avgHealth;
    public final float pestRate;

    private FarmStats(int total, float moisture, float fertility, float health, float pestRate) {
        this.totalPlots = total;
        this.avgMoisture = moisture;
        this.avgFertility = fertility;
        this.avgHealth = health;
        this.pestRate = pestRate;
    }

    public static FarmStats compute(ServerWorld world, BlockPos terminalPos) {
        SoilManager manager = SoilManager.get(world);
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    BlockPos candidate = terminalPos.add(dx, dy, dz);
                    if (world.getBlockState(candidate).getBlock() instanceof FarmlandBlock) {
                        manager.getOrCreate(candidate);
                        queue.add(candidate.toImmutable());
                    }
                }
            }
        }

        double m = 0, f = 0, h = 0;
        int pests = 0;

        while (!queue.isEmpty() && visited.size() < 256) {
            BlockPos pos = queue.poll();
            if (!visited.add(pos)) continue;
            SoilData soil = manager.get(pos);
            if (soil == null) continue;
            m += soil.getMoisture();
            f += soil.getFertility();
            h += soil.getHealth();
            if (soil.getPestType() != null) pests++;
            for (BlockPos n : new BlockPos[]{pos.north(), pos.south(), pos.east(), pos.west()}) {
                if (!visited.contains(n) && manager.get(n) != null) queue.add(n);
            }
        }

        int n = visited.size();
        if (n == 0) return new FarmStats(0, 0, 0, 0, 0);
        return new FarmStats(n, (float)(m/n), (float)(f/n), (float)(h/n), (float)pests/n*100);
    }
}

