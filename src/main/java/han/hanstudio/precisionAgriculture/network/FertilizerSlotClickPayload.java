package han.hanstudio.precisionAgriculture.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record FertilizerSlotClickPayload(BlockPos pos, int button) implements CustomPayload {
    public static final Id<FertilizerSlotClickPayload> ID = new Id<>(Identifier.of("precision-agriculture", "fertilizer_slot_click"));
    public static final PacketCodec<RegistryByteBuf, FertilizerSlotClickPayload> CODEC = PacketCodec.of(
            (v, buf) -> { buf.writeBlockPos(v.pos()); buf.writeInt(v.button()); },
            buf -> new FertilizerSlotClickPayload(buf.readBlockPos(), buf.readInt())
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
