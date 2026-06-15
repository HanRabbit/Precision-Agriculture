package han.hanstudio.precisionAgriculture.jade;

import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.minecraft.block.CropBlock;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class PrecisionAgricultureJadeClientPlugin implements IWailaPlugin {

    public static final Identifier CROP_ID = Identifier.of("precision-agriculture", "crop");

    @Override
    public void register(IWailaCommonRegistration reg) {
        reg.registerBlockDataProvider(CropDataProvider.INSTANCE, CropBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration reg) {
        reg.registerBlockComponent(CropTooltipProvider.INSTANCE, CropBlock.class);
    }

    public enum CropDataProvider implements IServerDataProvider<BlockAccessor> {
        INSTANCE;

        @Override
        public void appendServerData(NbtCompound nbt, BlockAccessor accessor) {
            if (!(accessor.getLevel() instanceof ServerWorld sw)) return;
            SoilData soil = SoilManager.get(sw).get(accessor.getPosition().down());
            if (soil != null) {
                if (soil.getPestType() != null)
                    nbt.putString("pestType", soil.getPestType());
                nbt.putFloat("fertility", soil.getFertility());
                nbt.putFloat("moisture", soil.getMoisture());
            }
            if (accessor.getBlockState().getBlock() instanceof CropBlock crop) {
                accessor.getBlockState().getProperties().stream()
                    .filter(p -> p.getName().equals("age"))
                    .findFirst()
                    .ifPresent(prop -> {
                        nbt.putInt("age", accessor.getBlockState().get((net.minecraft.state.property.IntProperty) prop));
                        nbt.putInt("maxAge", crop.getMaxAge());
                    });
            }
        }

        @Override
        public Identifier getUid() { return CROP_ID; }
    }

    public enum CropTooltipProvider implements IBlockComponentProvider {
        INSTANCE;

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            NbtCompound nbt = accessor.getServerData();
            if (nbt.contains("age") && nbt.contains("maxAge")) {
                int age = nbt.getInt("age").orElse(0);
                int max = nbt.getInt("maxAge").orElse(1);
                int percent = max > 0 ? age * 100 / max : 0;
                tooltip.add(Text.literal("§7生长: §a" + percent + "%"));
            }
            if (nbt.contains("fertility")) {
                float fertility = nbt.getFloat("fertility").orElse(0f);
                tooltip.add(Text.literal(String.format("§7肥力: §e%.1f%%", fertility)));
            }
            if (nbt.contains("moisture")) {
                float moisture = nbt.getFloat("moisture").orElse(0f);
                tooltip.add(Text.literal(String.format("§7湿度: §b%.1f%%", moisture)));
            }
            if (nbt.contains("pestType")) {
                String pest = nbt.getString("pestType").orElse("");
                tooltip.add(Text.literal("§7病虫害: §c" + pest));
            } else if (nbt.contains("age")) {
                tooltip.add(Text.literal("§7病虫害: §a无"));
            }
        }

        @Override
        public Identifier getUid() { return CROP_ID; }
    }
}
