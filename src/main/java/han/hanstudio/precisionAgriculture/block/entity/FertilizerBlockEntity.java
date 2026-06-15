package han.hanstudio.precisionAgriculture.block.entity;

import han.hanstudio.precisionAgriculture.ModBlockEntities;
import han.hanstudio.precisionAgriculture.ModRegistries;
import han.hanstudio.precisionAgriculture.machine.FertilizerMachine;
import han.hanstudio.precisionAgriculture.network.OpenFertilizerPayload;
import han.hanstudio.precisionAgriculture.network.SyncFertilizerPayload;
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

public class FertilizerBlockEntity extends BlockEntity {
    private int range = 3;
    private FertilizerMachine machine;
    private int broadcastCount = 0;

    public FertilizerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FERTILIZER, pos, state);
    }

    public int getRange() { return range; }
    public void setRange(int r) {
        range = Math.max(1, Math.min(5, r));
        if (machine != null) machine.setRange(range);
        markDirty();
    }
    public int getFertilizedLast() { return machine == null ? 0 : machine.getFertilizedLast(); }
    public SimpleInventory getInventory() { return machine == null ? new SimpleInventory(1) : machine.getInventory(); }

    public void sendOpenPayload(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new OpenFertilizerPayload(getPos(), range, getFertilizedLast()));
    }

    public void handleSlotClick(ServerPlayerEntity player, int button) {
        if (machine == null) machine = new FertilizerMachine(pos, range);
        SimpleInventory inv = machine.getInventory();
        ItemStack slotStack = inv.getStack(0);
        ItemStack held = player.getStackInHand(Hand.MAIN_HAND);

        if (button == 0) {
            if (held.isEmpty()) return;
            if (held.getItem() != ModRegistries.FERTILIZER_ITEM) {
                player.sendMessage(net.minecraft.text.Text.literal("§e此槽位仅接受肥料。"), true);
                return;
            }
            if (slotStack.isEmpty()) {
                inv.setStack(0, held.copy());
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
                inv.setStack(0, held.copy());
                player.setStackInHand(Hand.MAIN_HAND, copy);
            }
        } else {
            if (slotStack.isEmpty()) return;
            if (!player.giveItemStack(slotStack)) {
                player.dropItem(slotStack, false);
            }
            inv.setStack(0, ItemStack.EMPTY);
        }
        markDirty();
    }

    public void dropContents() {
        if (machine == null || world == null) return;
        SimpleInventory inv = machine.getInventory();
        ItemStack s = inv.getStack(0);
        if (!s.isEmpty()) net.minecraft.block.Block.dropStack(world, pos, s);
        inv.clear();
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        range = view.getInt("range", range);
        if (machine == null) machine = new FertilizerMachine(pos, range);
        ItemStack stack = view.read("slot0", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        machine.getInventory().setStack(0, stack);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putInt("range", range);
        if (machine != null) {
            ItemStack stack = machine.getInventory().getStack(0);
            if (!stack.isEmpty()) view.put("slot0", ItemStack.CODEC, stack);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, FertilizerBlockEntity be) {
        if (world.isClient()) return;
        if (be.machine == null) be.machine = new FertilizerMachine(pos, be.range);
        be.machine.serverTick((ServerWorld) world);

        if (++be.broadcastCount >= 20) {
            be.broadcastCount = 0;
            ItemStack stack = be.machine.getInventory().getStack(0);
            SyncFertilizerPayload payload = new SyncFertilizerPayload(
                    pos, be.range, be.getFertilizedLast(),
                    stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
            for (ServerPlayerEntity p : ((ServerWorld) world).getPlayers()) {
                if (p.getBlockPos().getSquaredDistance(pos) <= 32 * 32)
                    ServerPlayNetworking.send(p, payload);
            }
        }
    }
}
