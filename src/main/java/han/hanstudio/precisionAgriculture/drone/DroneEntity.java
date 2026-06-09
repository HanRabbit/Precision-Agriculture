package han.hanstudio.precisionAgriculture.drone;

import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class DroneEntity extends PathAwareEntity {
    private final List<BlockPos> patrolRoute = new ArrayList<>();
    private int routeIndex = 0;
    private int idleTicks = 0;

    public DroneEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setNoGravity(true);
    }

    public void addWaypoint(BlockPos pos) { patrolRoute.add(pos.toImmutable()); }

    @Override
    public void tick() {
        super.tick();
        World w = getEntityWorld();
        if (w.isClient() || patrolRoute.isEmpty()) return;
        ServerWorld sw = (ServerWorld) w;

        BlockPos target = patrolRoute.get(routeIndex);
        Vec3d targetVec = Vec3d.ofCenter(target).add(0, 2, 0);
        Vec3d delta = targetVec.subtract(getEyePos());

        if (delta.length() < 1.0) {
            scanAndAct(sw, target);
            idleTicks++;
            if (idleTicks > 20) { routeIndex = (routeIndex + 1) % patrolRoute.size(); idleTicks = 0; }
        } else {
            move(MovementType.SELF, delta.normalize().multiply(0.3));
        }
    }

    private void scanAndAct(ServerWorld world, BlockPos center) {
        SoilManager manager = SoilManager.get(world);
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                SoilData soil = manager.get(center.add(x, -2, z));
                if (soil == null) continue;
                if (soil.getPestType() != null) soil.setPestType(null);
                if (soil.getFertility() < 40f) soil.setFertility(soil.getFertility() + 10f);
            }
        }
        manager.markDirty();
    }

    @Override
    protected void initGoals() {}
}
