package han.hanstudio.precisionAgriculture.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record OpenFertilizerPayload(BlockPos pos, int range, int fertilizedLast) implements CustomPayload {
    public static final Id<OpenFertilizerPayload> ID = new Id<>(Identifier.of("precision-agriculture", "open_fertilizer"));
    public static final PacketCodec<RegistryByteBuf, OpenFertilizerPayload> CODEC = PacketCodec.of(
            (v, buf) -> { buf.writeBlockPos(v.pos()); buf.writeInt(v.range()); buf.writeInt(v.fertilizedLast()); },
            buf -> new OpenFertilizerPayload(buf.readBlockPos(), buf.readInt(), buf.readInt())
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
