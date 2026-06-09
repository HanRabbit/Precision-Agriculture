package han.hanstudio.precisionAgriculture.crop;

public class CornCrop extends Crop {
    public CornCrop() { super("corn"); }
    @Override public float getOptimalMoisture() { return 70f; }
    @Override public float getOptimalFertility() { return 70f; }
    @Override public float getMinTemperature() { return 20f; }
    @Override public float getMaxTemperature() { return 30f; }
}
