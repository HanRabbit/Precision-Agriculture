package han.hanstudio.precisionAgriculture.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record OpenPlanterPayload(BlockPos pos, int range, int plantedLast) implements CustomPayload {
    public static final Id<OpenPlanterPayload> ID = new Id<>(Identifier.of("precision-agriculture", "open_planter"));
    public static final PacketCodec<RegistryByteBuf, OpenPlanterPayload> CODEC = PacketCodec.of(
            (v, buf) -> { buf.writeBlockPos(v.pos()); buf.writeInt(v.range()); buf.writeInt(v.plantedLast()); },
            buf -> new OpenPlanterPayload(buf.readBlockPos(), buf.readInt(), buf.readInt())
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
