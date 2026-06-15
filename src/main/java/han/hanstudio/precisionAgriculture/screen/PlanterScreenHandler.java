package han.hanstudio.precisionAgriculture.screen;

import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class PlanterScreenHandler extends BaseInventoryScreenHandler {
    private static final int PLANTER_SLOTS = 9;

    public PlanterScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, new SimpleInventory(PLANTER_SLOTS));
    }

    public PlanterScreenHandler(int syncId, PlayerInventory playerInv, Inventory inv) {
        super(ModScreenHandlers.PLANTER, syncId, playerInv, inv, PLANTER_SLOTS);

        // 添加9个种子输入槽（3x3布局），只接受种子
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new Slot(inv, col + row * 3, 62 + col * 18, 17 + row * 18) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return isSeed(stack.getItem());
                    }
                });
            }
        }

        // 添加玩家物品栏
        addPlayerInventorySlots(playerInv, 8, 84);
    }

    /**
     * 检查物品是否是种子（可以种植的作物）
     */
    private static boolean isSeed(Item item) {
        for (Block block : net.minecraft.registry.Registries.BLOCK) {
            if (block instanceof CropBlock && block.asItem() != net.minecraft.item.Items.AIR
                    && block.asItem() == item) {
                return true;
            }
        }
        return false;
    }
}
