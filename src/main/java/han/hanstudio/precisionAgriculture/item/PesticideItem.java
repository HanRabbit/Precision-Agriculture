package han.hanstudio.precisionAgriculture.item;

import han.hanstudio.precisionAgriculture.pest.PestSystem;
import han.hanstudio.precisionAgriculture.pest.PestType;
import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PesticideItem extends Item {
    private final boolean isInsecticide;

    public PesticideItem(Settings settings, boolean isInsecticide) {
        super(settings);
        this.isInsecticide = isInsecticide;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        World world = ctx.getWorld();
        if (world.isClient()) return ActionResult.SUCCESS;
        PlayerEntity player = ctx.getPlayer();
        ServerWorld sw = (ServerWorld) world;
        BlockPos pos = ctx.getBlockPos();

        // Support clicking on the crop directly — resolve down to the farmland pos
        BlockPos soilPos = world.getBlockState(pos).getBlock() instanceof CropBlock ? pos.down() : pos;
        SoilData soil = SoilManager.get(sw).get(soilPos);

        if (soil == null || soil.getPestType() == null) {
            if (player != null) player.sendMessage(Text.literal("§7此处无病虫害。"), true);
            return ActionResult.PASS;
        }

        boolean cured = PestSystem.treatWithPesticide(soil, isInsecticide);
        if (cured) {
            // Remove the infected crop
            BlockPos cropPos = soilPos.up();
            if (world.getBlockState(cropPos).getBlock() instanceof CropBlock) {
                world.breakBlock(cropPos, true);
            }
            if (player != null) player.sendMessage(Text.literal("§a病虫害已清除，病株已移除！"), true);
            if (!player.isCreative()) ctx.getStack().decrement(1);
        } else {
            if (player != null) player.sendMessage(Text.literal(
                    "§e该农药对 " + PestType.fromName(soil.getPestType()).displayName + " 无效。"), true);
        }
        return ActionResult.SUCCESS;
    }
}
