package han.hanstudio.precisionAgriculture.soil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import han.hanstudio.precisionAgriculture.soil.SoilData;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SoilManager extends PersistentState {

    private final Map<BlockPos, SoilData> soilMap;

    private static final Codec<SoilData> SOIL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("moisture").forGetter(SoilData::getMoisture),
            Codec.FLOAT.fieldOf("fertility").forGetter(SoilData::getFertility),
            Codec.FLOAT.fieldOf("temperature").forGetter(SoilData::getTemperature),
            Codec.FLOAT.fieldOf("health").forGetter(SoilData::getHealth),
            Codec.STRING.optionalFieldOf("pestType", "").forGetter(d -> d.getPestType() == null ? "" : d.getPestType())
    ).apply(instance, (m, f, t, h, p) -> {
        SoilData s = new SoilData();
        s.setMoisture(m); s.setFertility(f); s.setTemperature(t); s.setHealth(h);
        s.setPestType(p.isEmpty() ? null : p);
        return s;
    }));

    private static final Codec<SoilManager> CODEC = Codec.unboundedMap(
            Codec.STRING.xmap(
                    key -> { String[] p = key.split(","); return new BlockPos(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2])); },
                    pos -> pos.getX() + "," + pos.getY() + "," + pos.getZ()
            ),
            SOIL_CODEC
    ).xmap(
            map -> { SoilManager m = new SoilManager(new HashMap<>(map)); return m; },
            manager -> manager.soilMap
    );

    public static final PersistentStateType<SoilManager> TYPE = new PersistentStateType<>(
            "precision_agriculture_soil",
            () -> new SoilManager(new HashMap<>()),
            CODEC,
            DataFixTypes.SAVED_DATA_SCOREBOARD
    );

    private SoilManager(Map<BlockPos, SoilData> map) {
        this.soilMap = map;
    }

    public SoilData getOrCreate(BlockPos pos) {
        return soilMap.computeIfAbsent(pos.toImmutable(), p -> new SoilData());
    }

    public SoilData get(BlockPos pos) {
        return soilMap.get(pos.toImmutable());
    }

    public Map<BlockPos, SoilData> getAll() {
        return soilMap;
    }

    public void tickAll(boolean isRaining) {
        soilMap.values().forEach(d -> d.tick(isRaining));
        markDirty();
    }

    public static SoilManager get(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(TYPE);
    }
}
