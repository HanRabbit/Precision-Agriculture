package han.hanstudio.precisionAgriculture.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SetPesticideSprayerRangePayload(BlockPos pos, int range) implements CustomPayload {
    public static final Id<SetPesticideSprayerRangePayload> ID = new Id<>(Identifier.of("precision-agriculture", "set_pesticide_sprayer_range"));
    public static final PacketCodec<RegistryByteBuf, SetPesticideSprayerRangePayload> CODEC = PacketCodec.of(
            (v, buf) -> { buf.writeBlockPos(v.pos()); buf.writeInt(v.range()); },
            buf -> new SetPesticideSprayerRangePayload(buf.readBlockPos(), buf.readInt())
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
