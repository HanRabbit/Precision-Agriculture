package han.hanstudio.precisionAgriculture.advisor;

import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvisorEngine {

    public static List<String> analyze(ServerWorld world) {
        SoilManager manager = SoilManager.get(world);
        Map<BlockPos, SoilData> all = manager.getAll();
        List<String> advice = new ArrayList<>();

        if (all.isEmpty()) { advice.add("暂无农田数据。"); return advice; }

        double totalMoisture = 0, totalFertility = 0, totalHealth = 0;
        int pestCount = 0;
        for (SoilData soil : all.values()) {
            totalMoisture += soil.getMoisture();
            totalFertility += soil.getFertility();
            totalHealth += soil.getHealth();
            if (soil.getPestType() != null) pestCount++;
        }
        int count = all.size();
        float avgMoisture = (float) (totalMoisture / count);
        float avgFertility = (float) (totalFertility / count);
        float pestRate = (float) pestCount / count * 100;

        if (avgMoisture < 40f) advice.add("平均湿度偏低(" + f(avgMoisture) + "%)，建议开启灌溉系统。");
        if (avgFertility < 30f) advice.add("平均肥力不足(" + f(avgFertility) + "%)，建议自动施肥机补充肥力。");
        if (pestRate > 10f) advice.add("农田病虫害率 " + f(pestRate) + "%，请及时使用农药处理。");
        if (pestRate > 30f) advice.add("警告：病虫害扩散严重，预计减产 " + f(pestRate * 0.4f) + "%。");

        float yieldEstimate = Math.max(0, 100 - (100 - avgMoisture) * 0.3f
                - (100 - avgFertility) * 0.3f - pestRate * 0.4f);
        advice.add("预计本轮收成产量指数：" + f(yieldEstimate) + "%");

        if (advice.size() == 1) advice.add(0, "农场状态良好，无需额外干预。");
        return advice;
    }

    private static String f(float v) { return String.format("%.1f", v); }
}
