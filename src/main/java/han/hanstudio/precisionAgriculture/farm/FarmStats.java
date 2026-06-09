package han.hanstudio.precisionAgriculture.farm;

import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

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

    public static FarmStats compute(ServerWorld world) {
        Map<BlockPos, SoilData> all = SoilManager.get(world).getAll();
        if (all.isEmpty()) return new FarmStats(0, 0, 0, 0, 0);

        double m = 0, f = 0, h = 0;
        int pests = 0;
        for (SoilData d : all.values()) {
            m += d.getMoisture();
            f += d.getFertility();
            h += d.getHealth();
            if (d.getPestType() != null) pests++;
        }
        int n = all.size();
        return new FarmStats(n, (float)(m/n), (float)(f/n), (float)(h/n), (float)pests/n*100);
    }
}
