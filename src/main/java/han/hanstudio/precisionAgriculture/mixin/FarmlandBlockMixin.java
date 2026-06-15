package han.hanstudio.precisionAgriculture.mixin;

import han.hanstudio.precisionAgriculture.block.entity.IrrigatorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin {

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void skipIfIrrigated(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        for (Map.Entry<BlockPos, Integer> entry : IrrigatorBlockEntity.rangeMap.entrySet()) {
            BlockPos irrigatorPos = entry.getKey();
            int range = entry.getValue();
            if (Math.abs(irrigatorPos.getX() - pos.getX()) <= range
                    && Math.abs(irrigatorPos.getZ() - pos.getZ()) <= range
                    && irrigatorPos.getY() - pos.getY() >= 0
                    && irrigatorPos.getY() - pos.getY() <= 3) {
                ci.cancel();
                return;
            }
        }
    }
}
