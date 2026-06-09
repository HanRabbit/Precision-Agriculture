package han.hanstudio.precisionAgriculture;

import han.hanstudio.precisionAgriculture.block.*;
import han.hanstudio.precisionAgriculture.block.entity.*;
import han.hanstudio.precisionAgriculture.drone.DroneEntity;
import han.hanstudio.precisionAgriculture.item.FertilizerItem;
import han.hanstudio.precisionAgriculture.item.PesticideItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModRegistries {

    public static Block SOIL_SENSOR_BLOCK;
    public static Block IRRIGATOR_BLOCK;
    public static Block FERTILIZER_BLOCK;
    public static Block WEATHER_STATION_BLOCK;
    public static Block AGRI_TERMINAL_BLOCK;

    public static Item FERTILIZER_ITEM;
    public static Item FUNGICIDE_ITEM;
    public static Item INSECTICIDE_ITEM;

    public static void register() {
        SOIL_SENSOR_BLOCK    = block("soil_sensor",       new SoilSensorBlock(settings("soil_sensor", 1f)));
        IRRIGATOR_BLOCK      = block("irrigator",         new IrrigatorBlock(settings("irrigator", 2f)));
        FERTILIZER_BLOCK     = block("fertilizer_machine",new FertilizerBlock(settings("fertilizer_machine", 2f)));
        WEATHER_STATION_BLOCK= block("weather_station",   new WeatherStationBlock(settings("weather_station", 2f)));
        AGRI_TERMINAL_BLOCK  = block("agri_terminal",     new AgriTerminalBlock(settings("agri_terminal", 3f)));

        FERTILIZER_ITEM  = item("fertilizer",  new FertilizerItem(itemSettings("fertilizer").maxCount(64)));
        FUNGICIDE_ITEM   = item("fungicide",   new PesticideItem(itemSettings("fungicide").maxCount(16), false));
        INSECTICIDE_ITEM = item("insecticide", new PesticideItem(itemSettings("insecticide").maxCount(16), true));

        ModBlockEntities.SOIL_SENSOR = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("soil_sensor"),
                FabricBlockEntityTypeBuilder.create(SoilSensorBlockEntity::new, SOIL_SENSOR_BLOCK).build());
        ModBlockEntities.IRRIGATOR = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("irrigator"),
                FabricBlockEntityTypeBuilder.create(IrrigatorBlockEntity::new, IRRIGATOR_BLOCK).build());
        ModBlockEntities.FERTILIZER = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("fertilizer_machine"),
                FabricBlockEntityTypeBuilder.create(FertilizerBlockEntity::new, FERTILIZER_BLOCK).build());
        ModBlockEntities.WEATHER_STATION = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("weather_station"),
                FabricBlockEntityTypeBuilder.create(WeatherStationBlockEntity::new, WEATHER_STATION_BLOCK).build());
        ModBlockEntities.AGRI_TERMINAL = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("agri_terminal"),
                FabricBlockEntityTypeBuilder.create(AgriTerminalBlockEntity::new, AGRI_TERMINAL_BLOCK).build());

        RegistryKey<EntityType<?>> droneKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id("drone"));
        ModEntities.DRONE = Registry.register(Registries.ENTITY_TYPE, droneKey,
                EntityType.Builder.<DroneEntity>create(DroneEntity::new, SpawnGroup.MISC)
                        .dimensions(0.8f, 0.5f).build(droneKey));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(e -> {
            e.add(SOIL_SENSOR_BLOCK.asItem());
            e.add(IRRIGATOR_BLOCK.asItem());
            e.add(FERTILIZER_BLOCK.asItem());
            e.add(WEATHER_STATION_BLOCK.asItem());
            e.add(AGRI_TERMINAL_BLOCK.asItem());
            e.add(FERTILIZER_ITEM);
            e.add(FUNGICIDE_ITEM);
            e.add(INSECTICIDE_ITEM);
        });
    }

    private static AbstractBlock.Settings settings(String name, float strength) {
        return AbstractBlock.Settings.create()
                .strength(strength)
                .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id(name)));
    }

    private static Item.Settings itemSettings(String name) {
        return new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id(name)));
    }

    private static Block block(String name, Block b) {
        Registry.register(Registries.BLOCK, id(name), b);
        Registry.register(Registries.ITEM, id(name),
                new BlockItem(b, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id(name)))));
        return b;
    }

    private static Item item(String name, Item i) {
        return Registry.register(Registries.ITEM, id(name), i);
    }

    private static Identifier id(String name) {
        return Identifier.of("precision-agriculture", name);
    }
}
