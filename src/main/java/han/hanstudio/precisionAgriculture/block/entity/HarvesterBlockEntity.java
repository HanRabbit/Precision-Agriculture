package han.hanstudio.precisionAgriculture.block.entity;

import han.hanstudio.precisionAgriculture.ModBlockEntities;
import han.hanstudio.precisionAgriculture.machine.Harvester;
import han.hanstudio.precisionAgriculture.network.SyncMachineStatusPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HarvesterBlockEntity extends BlockEntity {
    private int range = 3;
    private Harvester machine;
    private int broadcastCount = 0;

    public HarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HARVESTER, pos, state);
    }

    public int getRange() { return range; }
    public void setRange(int r) {
        range = Math.max(1, Math.min(5, r));
        if (machine != null) machine.setRange(range);
        markDirty();
    }
    public int getHarvestedLast() { return machine == null ? 0 : machine.getHarvestedLast(); }
    public SimpleInventory getInventory() { return machine == null ? new SimpleInventory(9) : machine.getInventory(); }

    public void openScreen(ServerPlayerEntity player) {
        player.openHandledScreen(new SimpleInventoryScreenHandlerFactory());
    }

    public void handleSlotClick(ServerPlayerEntity player, int slot, int button) {
        if (slot < 0 || slot >= 9) return;
        if (machine == null) machine = new Harvester(pos, range);
        SimpleInventory inv = machine.getInventory();
        ItemStack slotStack = inv.getStack(slot);
        ItemStack held = player.getStackInHand(Hand.MAIN_HAND);

        if (button == 0) {
            // 左键 - 输出槽只能取出不能放入
            return;
        } else {
            // 右键 - 取出物品
            if (slotStack.isEmpty()) return;
            if (!player.giveItemStack(slotStack)) {
                player.dropItem(slotStack, false);
            }
            inv.setStack(slot, ItemStack.EMPTY);
        }
        markDirty();
    }

    public void dropContents() {
        if (machine == null || world == null) return;
        SimpleInventory inv = machine.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            ItemStack s = inv.getStack(i);
            if (!s.isEmpty()) net.minecraft.block.Block.dropStack(world, pos, s);
        }
        inv.clear();
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        range = view.getInt("range", range);
        if (machine == null) machine = new Harvester(pos, range);
        for (int i = 0; i < 9; i++) {
            ItemStack stack = view.read("slot" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY);
            machine.getInventory().setStack(i, stack);
        }
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putInt("range", range);
        if (machine != null) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = machine.getInventory().getStack(i);
                if (!stack.isEmpty()) view.put("slot" + i, ItemStack.CODEC, stack);
            }
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, HarvesterBlockEntity be) {
        if (world.isClient()) return;
        if (be.machine == null) be.machine = new Harvester(pos, be.range);
        be.machine.serverTick((ServerWorld) world);

        if (++be.broadcastCount >= 20) {
            be.broadcastCount = 0;
            SyncMachineStatusPayload payload = new SyncMachineStatusPayload(pos, be.range, be.getHarvestedLast(), true);
            for (ServerPlayerEntity p : ((ServerWorld) world).getPlayers()) {
                if (p.getBlockPos().getSquaredDistance(pos) <= 32 * 32)
                    ServerPlayNetworking.send(p, payload);
            }
        }
    }

    private class SimpleInventoryScreenHandlerFactory implements net.minecraft.screen.NamedScreenHandlerFactory {
        @Override
        public net.minecraft.text.Text getDisplayName() {
            return net.minecraft.text.Text.literal("自动收割机");
        }

        @Override
        public net.minecraft.screen.ScreenHandler createMenu(int syncId, net.minecraft.entity.player.PlayerInventory playerInv, net.minecraft.entity.player.PlayerEntity player) {
            return new han.hanstudio.precisionAgriculture.screen.HarvesterScreenHandler(syncId, playerInv, getInventory());
        }
    }
}
