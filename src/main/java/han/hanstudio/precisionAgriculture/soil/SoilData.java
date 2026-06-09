package han.hanstudio.precisionAgriculture.soil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;

public class SoilData {
    private float moisture;
    private float fertility;
    private float temperature;
    private float health;
    private String pestType;
    private int bareTickCount;

    public SoilData() {
        this.moisture = 50f;
        this.fertility = 50f;
        this.temperature = 20f;
        this.health = 100f;
    }

    public void tick(boolean isRaining) {
        moisture = Math.max(0, moisture - 0.05f);
        if (isRaining) moisture = Math.min(100, moisture + 0.3f);
        if (pestType != null) health = Math.max(0, health - 0.1f);
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putFloat("moisture", moisture);
        nbt.putFloat("fertility", fertility);
        nbt.putFloat("temperature", temperature);
        nbt.putFloat("health", health);
        if (pestType != null) nbt.putString("pestType", pestType);
        return nbt;
    }

    public static SoilData fromNbt(NbtCompound nbt) {
        SoilData data = new SoilData();
        data.moisture = nbt.getFloat("moisture", 50f);
        data.fertility = nbt.getFloat("fertility", 50f);
        data.temperature = nbt.getFloat("temperature", 20f);
        data.health = nbt.getFloat("health", 100f);
        data.pestType = nbt.getString("pestType", null);
        return data;
    }

    public float getMoisture() { return moisture; }
    public float getFertility() { return fertility; }
    public float getTemperature() { return temperature; }
    public float getHealth() { return health; }
    public String getPestType() { return pestType; }

    public void setMoisture(float v) { moisture = Math.max(0, Math.min(100, v)); }
    public void setFertility(float v) { fertility = Math.max(0, Math.min(100, v)); }
    public void setTemperature(float v) { temperature = v; }
    public void setHealth(float v) { health = Math.max(0, Math.min(100, v)); }
    public void setPestType(String t) { pestType = t; }
    public int getBareTickCount() { return bareTickCount; }
    public void setBareTickCount(int v) { bareTickCount = v; }
}
