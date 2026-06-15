package han.hanstudio.precisionAgriculture.block.entity;

import han.hanstudio.precisionAgriculture.ModBlockEntities;
import han.hanstudio.precisionAgriculture.machine.Harvester;
import han.hanstudio.precisionAgriculture.network.SyncMachineStatusPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HarvesterBlockEntity extends BlockEntity {
    private int range = 3;
    private Harvester machine;
    private int broadcastCount = 0;

    public HarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HARVESTER, pos, state);
    }

    public int getRange() { return range; }
    public void setRange(int r) {
        range = Math.max(1, Math.min(5, r));
        if (machine != null) machine.setRange(range);
        markDirty();
    }
    public int getHarvestedLast() { return machine == null ? 0 : machine.getHarvestedLast(); }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        range = view.getInt("range", range);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putInt("range", range);
    }

    public static void tick(World world, BlockPos pos, BlockState state, HarvesterBlockEntity be) {
        if (world.isClient()) return;
        if (be.machine == null) be.machine = new Harvester(pos, be.range);
        be.machine.serverTick((ServerWorld) world);

        if (++be.broadcastCount >= 20) {
            be.broadcastCount = 0;
            SyncMachineStatusPayload payload = new SyncMachineStatusPayload(pos, be.range, be.getHarvestedLast(), true);
            for (ServerPlayerEntity p : ((ServerWorld) world).getPlayers()) {
                if (p.getBlockPos().getSquaredDistance(pos) <= 32 * 32)
                    ServerPlayNetworking.send(p, payload);
            }
        }
    }
}
