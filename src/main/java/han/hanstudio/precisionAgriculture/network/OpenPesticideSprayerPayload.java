package han.hanstudio.precisionAgriculture.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record OpenPesticideSprayerPayload(BlockPos pos, int range, int sprayedLast) implements CustomPayload {
    public static final Id<OpenPesticideSprayerPayload> ID = new Id<>(Identifier.of("precision-agriculture", "open_pesticide_sprayer"));
    public static final PacketCodec<RegistryByteBuf, OpenPesticideSprayerPayload> CODEC = PacketCodec.of(
            (v, buf) -> { buf.writeBlockPos(v.pos()); buf.writeInt(v.range()); buf.writeInt(v.sprayedLast()); },
            buf -> new OpenPesticideSprayerPayload(buf.readBlockPos(), buf.readInt(), buf.readInt())
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
