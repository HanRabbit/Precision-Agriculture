package han.hanstudio.precisionAgriculture.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SetFertilizerRangePayload(BlockPos pos, int range) implements CustomPayload {
    public static final Id<SetFertilizerRangePayload> ID = new Id<>(Identifier.of("precision-agriculture", "set_fertilizer_range"));
    public static final PacketCodec<RegistryByteBuf, SetFertilizerRangePayload> CODEC = PacketCodec.of(
            (v, buf) -> { buf.writeBlockPos(v.pos()); buf.writeInt(v.range()); },
            buf -> new SetFertilizerRangePayload(buf.readBlockPos(), buf.readInt())
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
