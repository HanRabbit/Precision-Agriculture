package han.hanstudio.precisionAgriculture.machine;

import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;

public class Harvester extends AgricultureMachine {
    private static final int TICK_INTERVAL = 40;
    private int tickCount = 0;
    private int harvestedLast = 0;
    private final SimpleInventory inventory = new SimpleInventory(9);

    public Harvester(BlockPos pos, int range) { super(pos, range); }

    public void setRange(int range) { this.range = range; }
    public int getHarvestedLast() { return harvestedLast; }
    public SimpleInventory getInventory() { return inventory; }

    @Override
    public void serverTick(ServerWorld world) {
        if (++tickCount < TICK_INTERVAL) return;
        tickCount = 0;
        harvestedLast = 0;
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int dy = -1; dy <= 3; dy++) {
                    BlockPos target = pos.add(x, dy, z);
                    BlockState state = world.getBlockState(target);
                    if (!(state.getBlock() instanceof CropBlock crop) || !crop.isMature(state)) continue;
                    List<ItemStack> drops = Block.getDroppedStacks(state, world, target, world.getBlockEntity(target), null, ItemStack.EMPTY);
                    world.breakBlock(target, false);
                    harvestedLast++;
                    if (world.getBlockState(target).isAir()) {
                        world.setBlockState(target, crop.getDefaultState());
                        boolean seeded = false;
                        for (ItemStack drop : drops) {
                            if (!seeded && drop.getItem() == crop.getDefaultState().getBlock().asItem()) {
                                seeded = true;
                                drop.decrement(1);
                                if (drop.isEmpty()) continue;
                            }
                            insertOrDrop(world, drop);
                        }
                    } else {
                        drops.forEach(d -> insertOrDrop(world, d));
                    }
                    break;
                }
            }
        }
    }

    private void insertOrDrop(ServerWorld world, ItemStack stack) {
        if (stack.isEmpty()) return;

        // 优先插入内部物品栏
        for (int i = 0; i < inventory.size() && !stack.isEmpty(); i++) {
            ItemStack slot = inventory.getStack(i);
            if (slot.isEmpty()) {
                inventory.setStack(i, stack.copyAndEmpty());
            } else if (ItemStack.areItemsAndComponentsEqual(slot, stack) && slot.getCount() < slot.getMaxCount()) {
                int transfer = Math.min(stack.getCount(), slot.getMaxCount() - slot.getCount());
                slot.increment(transfer);
                stack.decrement(transfer);
            }
        }

        // 如果内部物品栏满了，尝试相邻箱子
        if (!stack.isEmpty()) {
            Inventory chest = findAdjacentChest(world);
            if (chest != null) {
                for (int i = 0; i < chest.size() && !stack.isEmpty(); i++) {
                    ItemStack slot = chest.getStack(i);
                    if (slot.isEmpty()) {
                        chest.setStack(i, stack.copyAndEmpty());
                    } else if (ItemStack.areItemsAndComponentsEqual(slot, stack) && slot.getCount() < slot.getMaxCount()) {
                        int transfer = Math.min(stack.getCount(), slot.getMaxCount() - slot.getCount());
                        slot.increment(transfer);
                        stack.decrement(transfer);
                    }
                }
            }
        }

        // 最后才掉落
        if (!stack.isEmpty()) Block.dropStack(world, pos, stack);
    }

    private Inventory findAdjacentChest(ServerWorld world) {
        for (Direction dir : Direction.values()) {
            BlockPos p = pos.offset(dir);
            if (world.getBlockEntity(p) instanceof Inventory inv) return inv;
        }
        return null;
    }
}
