package han.hanstudio.precisionAgriculture.screen;

import han.hanstudio.precisionAgriculture.ModRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class FertilizerScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public FertilizerScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, new SimpleInventory(1));
    }

    public FertilizerScreenHandler(int syncId, PlayerInventory playerInv, Inventory inv) {
        super(ModScreenHandlers.FERTILIZER, syncId);
        this.inventory = inv;
        inv.onOpen(playerInv.player);

        addSlot(new Slot(inv, 0, 80, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == ModRegistries.FERTILIZER_ITEM;
            }
        });

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasStack()) return ItemStack.EMPTY;
        ItemStack stack = slot.getStack();
        ItemStack copy = stack.copy();
        if (index == 0) {
            if (!insertItem(stack, 1, 37, true)) return ItemStack.EMPTY;
        } else {
            if (stack.getItem() != ModRegistries.FERTILIZER_ITEM) return ItemStack.EMPTY;
            if (!insertItem(stack, 0, 1, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setStack(ItemStack.EMPTY);
        else slot.markDirty();
        return copy;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        inventory.onClose(player);
    }
}
