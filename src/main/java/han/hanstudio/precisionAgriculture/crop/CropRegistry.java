package han.hanstudio.precisionAgriculture.crop;

import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CropRegistry {
    private static final Map<String, Crop> REGISTRY = new HashMap<>();

    static {
        register(new WheatCrop());
        register(new CornCrop());
        register(new RiceCrop());
    }

    private static void register(Crop crop) { REGISTRY.put(crop.name, crop); }

    public static Crop get(String name) { return REGISTRY.get(name); }
    public static Map<String, Crop> all() { return REGISTRY; }

    /** Resolve a crop entry from a block identifier; falls back to wheat for unknown crops. */
    public static Crop findByBlockId(Identifier id) {
        if (id != null) {
            for (Crop c : REGISTRY.values()) if (c.matches(id)) return c;
        }
        return REGISTRY.get("wheat");
    }
}
