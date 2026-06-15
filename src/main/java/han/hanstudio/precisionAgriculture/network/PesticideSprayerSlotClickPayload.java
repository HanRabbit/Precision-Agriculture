package han.hanstudio.precisionAgriculture.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record PesticideSprayerSlotClickPayload(BlockPos pos, int slot, int button) implements CustomPayload {
    public static final Id<PesticideSprayerSlotClickPayload> ID =
            new Id<>(Identifier.of("precision-agriculture", "pesticide_sprayer_slot_click"));
    public static final PacketCodec<RegistryByteBuf, PesticideSprayerSlotClickPayload> CODEC = PacketCodec.of(
            (v, buf) -> { buf.writeBlockPos(v.pos()); buf.writeInt(v.slot()); buf.writeInt(v.button()); },
            buf -> new PesticideSprayerSlotClickPayload(buf.readBlockPos(), buf.readInt(), buf.readInt())
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
