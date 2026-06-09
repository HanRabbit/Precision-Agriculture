package han.hanstudio.precisionAgriculture.item;

import han.hanstudio.precisionAgriculture.pest.PestSystem;
import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
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
        SoilData soil = SoilManager.get((net.minecraft.server.world.ServerWorld) world)
                .get(ctx.getBlockPos());
        if (soil == null) {
            if (player != null) player.sendMessage(Text.literal("§c此处无农田土壤数据。"), true);
            return ActionResult.FAIL;
        }
        boolean cured = PestSystem.treatWithPesticide(soil, isInsecticide);
        if (player != null) {
            player.sendMessage(Text.literal(cured ? "§a病虫害已清除！" : "§e无匹配病害，未使用。"), true);
        }
        if (cured && !player.isCreative()) ctx.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }
}
