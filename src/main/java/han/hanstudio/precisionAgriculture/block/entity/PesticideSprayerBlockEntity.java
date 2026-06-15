package han.hanstudio.precisionAgriculture.block.entity;

import han.hanstudio.precisionAgriculture.ModBlockEntities;
import han.hanstudio.precisionAgriculture.ModRegistries;
import han.hanstudio.precisionAgriculture.machine.PesticideSprayer;
import han.hanstudio.precisionAgriculture.network.OpenPesticideSprayerPayload;
import han.hanstudio.precisionAgriculture.network.SyncPesticideSprayerPayload;
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

public class PesticideSprayerBlockEntity extends BlockEntity {
    private int range = 3;
    private PesticideSprayer machine;
    private int broadcastCount = 0;

    public PesticideSprayerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PESTICIDE_SPRAYER, pos, state);
    }

    public int getRange() { return range; }
    public void setRange(int r) {
        range = Math.max(1, Math.min(5, r));
        if (machine != null) machine.setRange(range);
        markDirty();
    }
    public int getSprayedLast() { return machine == null ? 0 : machine.getSprayedLast(); }
    public SimpleInventory getInventory() { return machine == null ? new SimpleInventory(2) : machine.getInventory(); }

    public void sendOpenPayload(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new OpenPesticideSprayerPayload(getPos(), range, getSprayedLast()));
    }

    public void handleSlotClick(ServerPlayerEntity player, int slot, int button) {
        if (slot < 0 || slot > 1) return;
        if (machine == null) machine = new PesticideSprayer(pos, range);
        SimpleInventory inv = machine.getInventory();
        ItemStack slotStack = inv.getStack(slot);
        ItemStack held = player.getStackInHand(Hand.MAIN_HAND);

        if (button == 0) {
            if (held.isEmpty()) return;
            if (!isValidForSlot(slot, held)) {
                player.sendMessage(net.minecraft.text.Text.literal(
                        slot == 0 ? "§e此槽位仅接受杀菌剂。" : "§e此槽位仅接受杀虫剂。"), true);
                return;
            }
            if (slotStack.isEmpty()) {
                inv.setStack(slot, held.copy());
                player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            } else if (ItemStack.areItemsAndComponentsEqual(slotStack, held)) {
                int max = Math.min(slotStack.getMaxCount(), inv.getMaxCountPerStack());
                int space = max - slotStack.getCount();
                int move = Math.min(space, held.getCount());
                if (move > 0) {
                    slotStack.increment(move);
                    held.decrement(move);
                }
            } else {
                ItemStack copy = slotStack.copy();
                inv.setStack(slot, held.copy());
                player.setStackInHand(Hand.MAIN_HAND, copy);
            }
        } else {
            if (slotStack.isEmpty()) return;
            if (!player.giveItemStack(slotStack)) {
                player.dropItem(slotStack, false);
            }
            inv.setStack(slot, ItemStack.EMPTY);
        }
        markDirty();
    }

    private static boolean isValidForSlot(int slot, ItemStack stack) {
        if (slot == 0) return stack.getItem() == ModRegistries.FUNGICIDE_ITEM;
        if (slot == 1) return stack.getItem() == ModRegistries.INSECTICIDE_ITEM;
        return false;
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
        if (machine == null) machine = new PesticideSprayer(pos, range);
        for (int i = 0; i < 2; i++) {
            ItemStack stack = view.read("slot" + i, ItemStack.CODEC).orElse(ItemStack.EMPTY);
            machine.getInventory().setStack(i, stack);
        }
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putInt("range", range);
        if (machine != null) {
            for (int i = 0; i < 2; i++) {
                ItemStack stack = machine.getInventory().getStack(i);
                if (!stack.isEmpty()) view.put("slot" + i, ItemStack.CODEC, stack);
            }
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, PesticideSprayerBlockEntity be) {
        if (world.isClient()) return;
        if (be.machine == null) be.machine = new PesticideSprayer(pos, be.range);
        be.machine.serverTick((ServerWorld) world);

        if (++be.broadcastCount >= 20) {
            be.broadcastCount = 0;
            SyncPesticideSprayerPayload payload = new SyncPesticideSprayerPayload(
                    pos, be.range, be.getSprayedLast(),
                    be.machine.getInventory().getStack(0).copy(),
                    be.machine.getInventory().getStack(1).copy());
            for (ServerPlayerEntity p : ((ServerWorld) world).getPlayers()) {
                if (p.getBlockPos().getSquaredDistance(pos) <= 32 * 32)
                    ServerPlayNetworking.send(p, payload);
            }
        }
    }
}
