package han.hanstudio.precisionAgriculture.crop;

public class WheatCrop extends Crop {
    public WheatCrop() { super("wheat"); }
    @Override public float getOptimalMoisture() { return 60f; }
    @Override public float getOptimalFertility() { return 50f; }
    @Override public float getMinTemperature() { return 15f; }
    @Override public float getMaxTemperature() { return 25f; }
}
