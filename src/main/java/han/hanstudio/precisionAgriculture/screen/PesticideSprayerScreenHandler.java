package han.hanstudio.precisionAgriculture.screen;

import han.hanstudio.precisionAgriculture.ModRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

/**
 * 农药播撒机的 ScreenHandler（2 个槽位：杀菌剂、杀虫剂）
 */
public class PesticideSprayerScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public PesticideSprayerScreenHandler(int syncId, PlayerInventory playerInv) {
        this(syncId, playerInv, new SimpleInventory(2));
    }

    public PesticideSprayerScreenHandler(int syncId, PlayerInventory playerInv, Inventory inv) {
        super(ModScreenHandlers.PESTICIDE_SPRAYER, syncId);
        this.inventory = inv;
        inv.onOpen(playerInv.player);

        // 杀菌剂槽（左侧）
        addSlot(new Slot(inv, 0, 44, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == ModRegistries.FUNGICIDE_ITEM;
            }
        });

        // 杀虫剂槽（右侧）
        addSlot(new Slot(inv, 1, 116, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem() == ModRegistries.INSECTICIDE_ITEM;
            }
        });

        // 玩家物品栏（3x9）
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        // 快捷栏（1x9）
        for (int i = 0; i < 9; i++)
            addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasStack()) return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        ItemStack copy = stack.copy();

        if (index < 2) {
            // 从机器到玩家物品栏
            if (!insertItem(stack, 2, 38, true))
                return ItemStack.EMPTY;
        } else {
            // 从玩家到机器
            if (stack.getItem() == ModRegistries.FUNGICIDE_ITEM) {
                if (!insertItem(stack, 0, 1, false))
                    return ItemStack.EMPTY;
            } else if (stack.getItem() == ModRegistries.INSECTICIDE_ITEM) {
                if (!insertItem(stack, 1, 2, false))
                    return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty())
            slot.setStack(ItemStack.EMPTY);
        else
            slot.markDirty();

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
