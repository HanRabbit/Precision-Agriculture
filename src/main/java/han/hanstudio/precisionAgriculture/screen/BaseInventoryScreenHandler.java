package han.hanstudio.precisionAgriculture.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

/**
 * 基础物品栏 ScreenHandler，封装通用的玩家物品栏槽位管理逻辑
 */
public abstract class BaseInventoryScreenHandler extends ScreenHandler {
    protected final Inventory inventory;
    protected final int machineSlotCount;

    protected BaseInventoryScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInv, Inventory inv, int machineSlotCount) {
        super(type, syncId);
        this.inventory = inv;
        this.machineSlotCount = machineSlotCount;
        inv.onOpen(playerInv.player);
    }

    /**
     * 添加玩家物品栏槽位（背包3x9 + 快捷栏1x9）
     * @param playerInv 玩家物品栏
     * @param startX 起始X坐标
     * @param startY 起始Y坐标（背包第一行）
     */
    protected void addPlayerInventorySlots(PlayerInventory playerInv, int startX, int startY) {
        // 玩家背包 3x9
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, startX + col * 18, startY + row * 18));
            }
        }

        // 玩家快捷栏 1x9
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, startX + col * 18, startY + 58));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getStack();
        ItemStack originalStack = stack.copy();

        // 机器槽位 -> 玩家物品栏
        if (index < machineSlotCount) {
            if (!insertItem(stack, machineSlotCount, machineSlotCount + 36, true)) {
                return ItemStack.EMPTY;
            }
        }
        // 玩家物品栏 -> 机器槽位
        else {
            if (!insertItem(stack, 0, machineSlotCount, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        return originalStack;
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
