package han.hanstudio.precisionAgriculture.network;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public record OpenAgriTerminalPayload(BlockPos pos, int totalPlots, float avgMoisture, float avgFertility,
                                       float avgHealth, float pestRate, List<String> advice) implements CustomPayload {

    public static final Id<OpenAgriTerminalPayload> ID = new Id<>(Identifier.of("precision-agriculture", "open_agri_terminal"));

    public static final PacketCodec<net.minecraft.network.RegistryByteBuf, OpenAgriTerminalPayload> CODEC = PacketCodec.of(
            (v, buf) -> {
                buf.writeBlockPos(v.pos());
                buf.writeInt(v.totalPlots());
                buf.writeFloat(v.avgMoisture()); buf.writeFloat(v.avgFertility());
                buf.writeFloat(v.avgHealth()); buf.writeFloat(v.pestRate());
                buf.writeInt(v.advice().size());
                v.advice().forEach(buf::writeString);
            },
            buf -> {
                BlockPos pos = buf.readBlockPos();
                int plots = buf.readInt();
                float m = buf.readFloat(), f = buf.readFloat(), h = buf.readFloat(), p = buf.readFloat();
                int n = buf.readInt();
                List<String> advice = new ArrayList<>();
                for (int i = 0; i < n; i++) advice.add(buf.readString());
                return new OpenAgriTerminalPayload(pos, plots, m, f, h, p, advice);
            }
    );

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
