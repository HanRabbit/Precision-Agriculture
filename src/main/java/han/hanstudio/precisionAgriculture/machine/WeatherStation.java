package han.hanstudio.precisionAgriculture.machine;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

public class WeatherStation extends AgricultureMachine {
    private float ambientTemperature;
    private int lightLevel;
    private boolean raining;

    public WeatherStation(BlockPos pos) { super(pos, 0); }

    @Override
    public void serverTick(ServerWorld world) {
        raining = world.isRaining();
        lightLevel = world.getLightLevel(LightType.SKY, pos.up());
        // Map biome temperature: MC uses 0-2 range roughly mapping to 0-40°C
        ambientTemperature = world.getBiome(pos).value().getTemperature() * 20f;
    }

    public float getAmbientTemperature() { return ambientTemperature; }
    public int getLightLevel() { return lightLevel; }
    public boolean isRaining() { return raining; }
}
