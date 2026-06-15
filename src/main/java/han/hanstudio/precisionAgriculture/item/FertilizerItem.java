package han.hanstudio.precisionAgriculture.item;

import han.hanstudio.precisionAgriculture.ModSounds;
import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FertilizerItem extends Item {
    private static final float AMOUNT = 20f;
    private static final Map<UUID, Long> lastSoundTime = new HashMap<>();

    public FertilizerItem(Settings settings) { super(settings); }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        World world = ctx.getWorld();
        if (world.isClient()) return ActionResult.SUCCESS;
        BlockPos pos = ctx.getBlockPos();
        // 支持点击作物，自动定位下方耕地
        BlockPos farmPos;
        if (world.getBlockState(pos).getBlock() instanceof CropBlock) {
            farmPos = pos.down();
        } else if (world.getBlockState(pos).getBlock() instanceof FarmlandBlock) {
            farmPos = pos;
        } else {
            return ActionResult.PASS;
        }
        if (!(world.getBlockState(farmPos).getBlock() instanceof FarmlandBlock)) return ActionResult.PASS;
        PlayerEntity player = ctx.getPlayer();
        SoilData soil = SoilManager.get((ServerWorld) world).getOrCreate(farmPos);
        soil.setFertility(soil.getFertility() + AMOUNT);
        if (player != null) {
            long now = world.getTime();
            UUID id = player.getUuid();
            if (now - lastSoundTime.getOrDefault(id, 0L) > 200) {
                world.playSound(null, farmPos, ModSounds.FERTILIZER_USE, SoundCategory.PLAYERS, 1f, 1f);
                lastSoundTime.put(id, now);
            }
        }
        if (player != null) player.sendMessage(
                Text.literal(String.format("§a施肥完成！当前肥力: %.1f%%", soil.getFertility())), true);
        if (player != null && !player.isCreative()) ctx.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }
}
