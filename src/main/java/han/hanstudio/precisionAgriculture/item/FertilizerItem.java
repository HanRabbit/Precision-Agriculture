package han.hanstudio.precisionAgriculture.item;

import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class FertilizerItem extends Item {
    private static final float AMOUNT = 20f;

    public FertilizerItem(Settings settings) { super(settings); }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        World world = ctx.getWorld();
        if (world.isClient()) return ActionResult.SUCCESS;
        PlayerEntity player = ctx.getPlayer();
        SoilData soil = SoilManager.get((net.minecraft.server.world.ServerWorld) world)
                .getOrCreate(ctx.getBlockPos());
        soil.setFertility(soil.getFertility() + AMOUNT);
        if (player != null) player.sendMessage(
                Text.literal(String.format("§a施肥完成！当前肥力: %.1f%%", soil.getFertility())), true);
        if (player != null && !player.isCreative()) ctx.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }
}
