package han.hanstudio.precisionAgriculture;

import han.hanstudio.precisionAgriculture.block.*;
import han.hanstudio.precisionAgriculture.block.entity.*;
import han.hanstudio.precisionAgriculture.drone.DroneEntity;
import han.hanstudio.precisionAgriculture.item.FertilizerItem;
import han.hanstudio.precisionAgriculture.item.PesticideItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModRegistries {

    public static Block SOIL_SENSOR_BLOCK;
    public static Block IRRIGATOR_BLOCK;
    public static Block FERTILIZER_BLOCK;
    public static Block WEATHER_STATION_BLOCK;
    public static Block AGRI_TERMINAL_BLOCK;
    public static Block HARVESTER_BLOCK;
    public static Block PLANTER_BLOCK;
    public static Block PESTICIDE_SPRAYER_BLOCK;
    public static Block MA_BAOGUO_STATUE_BLOCK;

    public static Item FERTILIZER_ITEM;
    public static Item FUNGICIDE_ITEM;
    public static Item INSECTICIDE_ITEM;
    public static Item AGRI_STAR_ITEM;

    public static void register() {
        SOIL_SENSOR_BLOCK    = block("soil_sensor",       new SoilSensorBlock(settings("soil_sensor", 1f)));
        IRRIGATOR_BLOCK      = block("irrigator",         new IrrigatorBlock(settings("irrigator", 2f)));
        FERTILIZER_BLOCK     = block("fertilizer_machine",new FertilizerBlock(settings("fertilizer_machine", 2f)));
        WEATHER_STATION_BLOCK= block("weather_station",   new WeatherStationBlock(settings("weather_station", 2f)));
        AGRI_TERMINAL_BLOCK  = block("agri_terminal",     new AgriTerminalBlock(settings("agri_terminal", 3f)));
        HARVESTER_BLOCK      = block("harvester",          new HarvesterBlock(settings("harvester", 2f)));
        PLANTER_BLOCK        = block("planter",            new PlanterBlock(settings("planter", 2f)));
        PESTICIDE_SPRAYER_BLOCK = block("pesticide_sprayer", new PesticideSprayerBlock(settings("pesticide_sprayer", 2f)));
        MA_BAOGUO_STATUE_BLOCK  = block("ma_baoguo_statue",  new MaBaoguoStatueBlock(settings("ma_baoguo_statue", 3f)));

        FERTILIZER_ITEM  = item("fertilizer",  new FertilizerItem(itemSettings("fertilizer").maxCount(64)));
        FUNGICIDE_ITEM   = item("fungicide",   new PesticideItem(itemSettings("fungicide").maxCount(16), false));
        INSECTICIDE_ITEM = item("insecticide", new PesticideItem(itemSettings("insecticide").maxCount(16), true));
        AGRI_STAR_ITEM   = item("agri_star",   new Item(itemSettings("agri_star").maxCount(64).rarity(net.minecraft.util.Rarity.COMMON)));

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
        ModBlockEntities.HARVESTER = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("harvester"),
                FabricBlockEntityTypeBuilder.create(HarvesterBlockEntity::new, HARVESTER_BLOCK).build());
        ModBlockEntities.PLANTER = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("planter"),
                FabricBlockEntityTypeBuilder.create(PlanterBlockEntity::new, PLANTER_BLOCK).build());
        ModBlockEntities.PESTICIDE_SPRAYER = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("pesticide_sprayer"),
                FabricBlockEntityTypeBuilder.create(PesticideSprayerBlockEntity::new, PESTICIDE_SPRAYER_BLOCK).build());

        RegistryKey<EntityType<?>> droneKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, id("drone"));
        ModEntities.DRONE = Registry.register(Registries.ENTITY_TYPE, droneKey,
                EntityType.Builder.<DroneEntity>create(DroneEntity::new, SpawnGroup.MISC)
                        .dimensions(0.8f, 0.5f).build(droneKey));

        RegistryKey<ItemGroup> groupKey = RegistryKey.of(RegistryKeys.ITEM_GROUP, id("precision_agriculture"));
        Registry.register(Registries.ITEM_GROUP, groupKey,
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemGroup.precision-agriculture.main"))
                        .icon(() -> new ItemStack(AGRI_TERMINAL_BLOCK))
                        .entries((ctx, e) -> {
                            e.add(SOIL_SENSOR_BLOCK);
                            e.add(IRRIGATOR_BLOCK);
                            e.add(FERTILIZER_BLOCK);
                            e.add(WEATHER_STATION_BLOCK);
                            e.add(AGRI_TERMINAL_BLOCK);
                            e.add(HARVESTER_BLOCK);
                            e.add(PLANTER_BLOCK);
                            e.add(PESTICIDE_SPRAYER_BLOCK);
                            e.add(MA_BAOGUO_STATUE_BLOCK);
                            e.add(FERTILIZER_ITEM);
                            e.add(AGRI_STAR_ITEM);
                            e.add(FUNGICIDE_ITEM);
                            e.add(INSECTICIDE_ITEM);
                        })
                        .build());
    }

    private static AbstractBlock.Settings settings(String name, float strength) {
        return AbstractBlock.Settings.create()
                .strength(strength)
                .sounds(BlockSoundGroup.METAL)
                .requiresTool()
                .registryKey(RegistryKey.of(RegistryKeys.BLOCK, id(name)));
    }

    private static Item.Settings itemSettings(String name) {
        return new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id(name)));
    }

    private static Block block(String name, Block b) {
        Registry.register(Registries.BLOCK, id(name), b);
        Registry.register(Registries.ITEM, id(name),
                new BlockItem(b, itemSettings(name)));
        return b;
    }

    private static Item item(String name, Item i) {
        return Registry.register(Registries.ITEM, id(name), i);
    }

    private static Identifier id(String name) {
        return Identifier.of("precision-agriculture", name);
    }
}
