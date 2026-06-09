package han.hanstudio.precisionAgriculture.crop;

import han.hanstudio.precisionAgriculture.soil.SoilData;
import net.minecraft.util.Identifier;

public abstract class Crop {
    public final String name;

    protected Crop(String name) {
        this.name = name;
    }

    public abstract float getOptimalMoisture();
    public abstract float getOptimalFertility();
    public abstract float getMinTemperature();
    public abstract float getMaxTemperature();

    /** Whether this Crop entry represents the given block identifier. */
    public boolean matches(Identifier blockId) {
        if (blockId == null) return false;
        String path = blockId.getPath();
        return path.equals(name) || path.contains(name);
    }

    /** Returns a growth multiplier 0.0 – 2.0 based on current soil conditions. */
    public float calcGrowthMultiplier(SoilData soil, int lightLevel) {
        if (soil.getPestType() != null) return 0.2f;

        float mScore = 1f - Math.abs(soil.getMoisture() - getOptimalMoisture()) / 100f;
        float fScore = 1f - Math.abs(soil.getFertility() - getOptimalFertility()) / 100f;

        float temp = soil.getTemperature();
        float optTemp = (getMinTemperature() + getMaxTemperature()) / 2f;
        float tempRange = (getMaxTemperature() - getMinTemperature()) / 2f;
        float tScore = Math.max(0, 1f - Math.abs(temp - optTemp) / tempRange);

        float lScore = lightLevel / 15f;

        return Math.max(0, (mScore + fScore + tScore + lScore) / 4f * 2f);
    }

    public float estimatedYield(SoilData soil) {
        return Math.min(1f, calcGrowthMultiplier(soil, 15) / 2f);
    }
}
