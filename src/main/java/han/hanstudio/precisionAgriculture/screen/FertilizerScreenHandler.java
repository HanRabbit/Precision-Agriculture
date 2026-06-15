package han.hanstudio.precisionAgriculture.screen;

import han.hanstudio.precisionAgriculture.ModRegistries;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class FertilizerScreenHandler extends BaseInventoryScreenHandler {
    public FertilizerScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, new SimpleInventory(1));
    }

    public FertilizerScreenHandler(int syncId, PlayerInventory playerInv, Inventory inv) {
        super(ModScreenHandlers.FERTILIZER, syncId, playerInv, inv, 1);

        addSlot(new Slot(inv, 0, 80, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == ModRegistries.FERTILIZER_ITEM;
            }
        });

        addPlayerInventorySlots(playerInv, 8, 84);
    }
}
