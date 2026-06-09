package han.hanstudio.precisionAgriculture.block;

import han.hanstudio.precisionAgriculture.block.entity.SoilSensorBlockEntity;
import han.hanstudio.precisionAgriculture.network.OpenSoilSensorPayload;
import han.hanstudio.precisionAgriculture.soil.SoilData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoilSensorBlock extends Block implements BlockEntityProvider {
    public SoilSensorBlock(Settings settings) { super(settings); }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SoilSensorBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;
        if (!(player instanceof ServerPlayerEntity sp)) return ActionResult.PASS;
        if (world.getBlockEntity(pos) instanceof SoilSensorBlockEntity be) {
            SoilData soil = be.getSoilBelow();
            ServerPlayNetworking.send(sp, new OpenSoilSensorPayload(
                    pos.down(), soil.getMoisture(), soil.getFertility(),
                    soil.getTemperature(), soil.getHealth(), soil.getPestType()));
        }
        return ActionResult.SUCCESS;
    }
}
