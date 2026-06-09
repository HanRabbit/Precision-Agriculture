package han.hanstudio.precisionAgriculture.crop;

public class RiceCrop extends Crop {
    public RiceCrop() { super("rice"); }
    @Override public float getOptimalMoisture() { return 90f; }
    @Override public float getOptimalFertility() { return 60f; }
    @Override public float getMinTemperature() { return 20f; }
    @Override public float getMaxTemperature() { return 35f; }
}
