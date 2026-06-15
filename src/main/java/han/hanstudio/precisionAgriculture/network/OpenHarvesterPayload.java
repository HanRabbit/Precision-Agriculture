package han.hanstudio.precisionAgriculture.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record OpenHarvesterPayload(BlockPos pos, int range, int harvestedLast) implements CustomPayload {
    public static final Id<OpenHarvesterPayload> ID = new Id<>(Identifier.of("precision-agriculture", "open_harvester"));
    public static final PacketCodec<RegistryByteBuf, OpenHarvesterPayload> CODEC = PacketCodec.of(
            (v, buf) -> { buf.writeBlockPos(v.pos()); buf.writeInt(v.range()); buf.writeInt(v.harvestedLast()); },
            buf -> new OpenHarvesterPayload(buf.readBlockPos(), buf.readInt(), buf.readInt())
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
