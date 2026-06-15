package han.hanstudio.precisionAgriculture.network;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SyncFertilizerPayload(BlockPos pos, int range, int fertilizedLast, ItemStack slot0) implements CustomPayload {
    public static final Id<SyncFertilizerPayload> ID = new Id<>(Identifier.of("precision-agriculture", "sync_fertilizer"));
    public static final PacketCodec<RegistryByteBuf, SyncFertilizerPayload> CODEC = PacketCodec.of(
            (v, buf) -> {
                buf.writeBlockPos(v.pos());
                buf.writeInt(v.range());
                buf.writeInt(v.fertilizedLast());
                ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, v.slot0().isEmpty() ? ItemStack.EMPTY : v.slot0());
            },
            buf -> new SyncFertilizerPayload(
                    buf.readBlockPos(),
                    buf.readInt(),
                    buf.readInt(),
                    ItemStack.OPTIONAL_PACKET_CODEC.decode(buf)
            )
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
