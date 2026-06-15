package han.hanstudio.precisionAgriculture.network;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SyncPesticideSprayerPayload(BlockPos pos, int range, int sprayedLast,
                                           ItemStack slot0, ItemStack slot1) implements CustomPayload {
    public static final Id<SyncPesticideSprayerPayload> ID = new Id<>(Identifier.of("precision-agriculture", "sync_pesticide_sprayer"));
    public static final PacketCodec<RegistryByteBuf, SyncPesticideSprayerPayload> CODEC = PacketCodec.of(
            (v, buf) -> {
                buf.writeBlockPos(v.pos()); buf.writeInt(v.range()); buf.writeInt(v.sprayedLast());
                ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, v.slot0());
                ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, v.slot1());
            },
            buf -> {
                BlockPos pos = buf.readBlockPos(); int range = buf.readInt(); int count = buf.readInt();
                ItemStack s0 = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
                ItemStack s1 = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
                return new SyncPesticideSprayerPayload(pos, range, count, s0, s1);
            }
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
