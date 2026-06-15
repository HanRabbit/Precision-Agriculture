package han.hanstudio.precisionAgriculture.screen;

import han.hanstudio.precisionAgriculture.ModRegistries;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class HarvesterScreenHandler extends BaseInventoryScreenHandler {
    private static final int HARVESTER_SLOTS = 9;

    public HarvesterScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, new SimpleInventory(HARVESTER_SLOTS));
    }

    public HarvesterScreenHandler(int syncId, PlayerInventory playerInv, Inventory inv) {
        super(ModScreenHandlers.HARVESTER, syncId, playerInv, inv, HARVESTER_SLOTS);

        // 添加9个输出槽（3x3布局），只能取出不能放入
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new Slot(inv, col + row * 3, 62 + col * 18, 17 + row * 18) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return false; // 输出槽，不允许放入
                    }
                });
            }
        }

        // 添加玩家物品栏
        addPlayerInventorySlots(playerInv, 8, 84);
    }
}
