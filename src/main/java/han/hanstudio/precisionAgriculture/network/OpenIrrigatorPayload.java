package han.hanstudio.precisionAgriculture.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record OpenIrrigatorPayload(BlockPos pos, int range) implements CustomPayload {
    public static final Id<OpenIrrigatorPayload> ID = new Id<>(Identifier.of("precision-agriculture", "open_irrigator"));
    public static final PacketCodec<RegistryByteBuf, OpenIrrigatorPayload> CODEC = PacketCodec.of(
            (v, buf) -> { buf.writeBlockPos(v.pos()); buf.writeInt(v.range()); },
            buf -> new OpenIrrigatorPayload(buf.readBlockPos(), buf.readInt())
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
