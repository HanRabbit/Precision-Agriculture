package han.hanstudio.precisionAgriculture.crop;

import han.hanstudio.precisionAgriculture.soil.SoilData;

public abstract class Crop {
    public final String name;

    protected Crop(String name) {
        this.name = name;
    }

    public abstract float getOptimalMoisture();
    public abstract float getOptimalFertility();
    public abstract float getMinTemperature();
    public abstract float getMaxTemperature();

    /** Returns a growth multiplier 0.0 – 2.0 based on current soil conditions. */
    public float calcGrowthMultiplier(SoilData soil) {
        if (soil.getPestType() != null) return 0.2f;

        float mScore = 1f - Math.abs(soil.getMoisture() - getOptimalMoisture()) / 100f;
        float fScore = 1f - Math.abs(soil.getFertility() - getOptimalFertility()) / 100f;

        float temp = soil.getTemperature();
        float optTemp = (getMinTemperature() + getMaxTemperature()) / 2f;
        float tempRange = (getMaxTemperature() - getMinTemperature()) / 2f;
        float tScore = Math.max(0, 1f - Math.abs(temp - optTemp) / tempRange);

        return Math.max(0, (mScore + fScore + tScore) / 3f * 2f);
    }

    /** Estimated final yield multiplier (0-1) based on growth potential. */
    public float estimatedYield(SoilData soil) {
        return Math.min(1f, calcGrowthMultiplier(soil) / 2f);
    }
}
