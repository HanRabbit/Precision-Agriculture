package han.hanstudio.precisionAgriculture.machine;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class Planter extends AgricultureMachine {
    private static final int TICK_INTERVAL = 60;
    private int tickCount = 0;
    private int plantedLast = 0;
    private final SimpleInventory inventory = new SimpleInventory(9);

    public Planter(BlockPos pos, int range) { super(pos, range); }

    public void setRange(int range) { this.range = range; }
    public int getPlantedLast() { return plantedLast; }
    public SimpleInventory getInventory() { return inventory; }

    @Override
    public void serverTick(ServerWorld world) {
        if (++tickCount < TICK_INTERVAL) return;
        tickCount = 0;
        plantedLast = 0;

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                for (int dy = -1; dy <= 3; dy++) {
                    BlockPos farmland = pos.add(x, dy, z);
                    if (!(world.getBlockState(farmland).getBlock() instanceof FarmlandBlock)) continue;
                    BlockPos above = farmland.up();
                    if (!world.getBlockState(above).isAir()) break;
                    if (plantFromInventory(world, above)) {
                        plantedLast++;
                    }
                    break;
                }
            }
        }
    }

    private boolean plantFromInventory(ServerWorld world, BlockPos above) {
        // 优先从内部物品栏获取种子
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack seed = inventory.getStack(slot);
            if (seed.isEmpty()) continue;
            Block cropBlock = getCropForSeed(seed.getItem());
            if (cropBlock instanceof CropBlock) {
                world.setBlockState(above, cropBlock.getDefaultState());
                seed.decrement(1);
                return true;
            }
        }

        // 如果内部物品栏没有种子，尝试相邻箱子
        Inventory chest = findAdjacentChest(world);
        if (chest != null) {
            for (int slot = 0; slot < chest.size(); slot++) {
                ItemStack seed = chest.getStack(slot);
                if (seed.isEmpty()) continue;
                Block cropBlock = getCropForSeed(seed.getItem());
                if (cropBlock instanceof CropBlock) {
                    world.setBlockState(above, cropBlock.getDefaultState());
                    seed.decrement(1);
                    return true;
                }
            }
        }

        return false;
    }

    /** Returns the crop block for a seed item using PlantableOnBlock tag logic. */
    private Block getCropForSeed(Item seed) {
        // Walk all blocks: find a CropBlock whose asItem() matches this seed
        for (Block block : net.minecraft.registry.Registries.BLOCK) {
            if (block instanceof CropBlock && block.asItem() != net.minecraft.item.Items.AIR
                    && block.asItem() == seed) {
                return block;
            }
        }
        return null;
    }

    private Inventory findAdjacentChest(ServerWorld world) {
        for (Direction dir : Direction.values()) {
            BlockPos p = pos.offset(dir);
            if (world.getBlockEntity(p) instanceof Inventory inv) return inv;
        }
        return null;
    }
}
