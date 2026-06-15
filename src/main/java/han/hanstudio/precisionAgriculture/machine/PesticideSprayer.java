package han.hanstudio.precisionAgriculture.machine;

import han.hanstudio.precisionAgriculture.item.PesticideItem;
import han.hanstudio.precisionAgriculture.pest.PestSystem;
import han.hanstudio.precisionAgriculture.soil.SoilData;
import han.hanstudio.precisionAgriculture.soil.SoilManager;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class PesticideSprayer extends AgricultureMachine {
    private static final int TICK_INTERVAL = 60;
    private int tickCount = 0;
    private int sprayedLast = 0;
    private final SimpleInventory inventory = new SimpleInventory(2);

    public PesticideSprayer(BlockPos pos, int range) { super(pos, range); }

    public void setRange(int r) { this.range = r; }
    public int getSprayedLast() { return sprayedLast; }
    public SimpleInventory getInventory() { return inventory; }

    @Override
    public void serverTick(ServerWorld world) {
        if (++tickCount < TICK_INTERVAL) return;
        tickCount = 0;
        sprayedLast = 0;
        SoilManager mgr = SoilManager.get(world);
        boolean dirty = false;
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int dy = -1; dy <= 3; dy++) {
                    BlockPos fp = pos.add(x, dy, z);
                    if (!(world.getBlockState(fp).getBlock() instanceof net.minecraft.block.FarmlandBlock)) continue;
                    SoilData soil = mgr.get(fp);
                    if (soil == null || soil.getPestType() == null) break;
                    if (trySpray(soil)) { sprayedLast++; dirty = true; }
                    break;
                }
            }
        }
        if (dirty) mgr.markDirty();
    }

    private boolean trySpray(SoilData soil) {
        han.hanstudio.precisionAgriculture.pest.PestType type =
                han.hanstudio.precisionAgriculture.pest.PestType.fromName(soil.getPestType());
        // slot 0 = fungicide (isDisease cures), slot 1 = insecticide
        int slot = type.isDisease ? 0 : 1;
        ItemStack stack = inventory.getStack(slot);
        if (stack.isEmpty() || !(stack.getItem() instanceof PesticideItem)) return false;
        boolean cured = PestSystem.treatWithPesticide(soil, !type.isDisease);
        if (cured) stack.decrement(1);
        return cured;
    }
}
