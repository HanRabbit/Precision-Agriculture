package han.hanstudio.precisionAgriculture.block;

import han.hanstudio.precisionAgriculture.block.entity.IrrigatorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import han.hanstudio.precisionAgriculture.ModBlockEntities;

public class IrrigatorBlock extends Block implements BlockEntityProvider {
    public IrrigatorBlock(Settings settings) { super(settings); }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new IrrigatorBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.IRRIGATOR
                ? (w, p, s, be) -> IrrigatorBlockEntity.tick(w, p, s, (IrrigatorBlockEntity) be)
                : null;
    }
}
