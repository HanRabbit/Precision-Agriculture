package han.hanstudio.precisionAgriculture.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record OpenSoilSensorPayload(BlockPos pos, float moisture, float fertility, float temperature, float health, String pestType) implements CustomPayload {

    public static final Id<OpenSoilSensorPayload> ID = new Id<>(Identifier.of("precision-agriculture", "open_soil_sensor"));

    public static final PacketCodec<net.minecraft.network.RegistryByteBuf, OpenSoilSensorPayload> CODEC = PacketCodec.of(
            (v, buf) -> {
                buf.writeBlockPos(v.pos());
                buf.writeFloat(v.moisture()); buf.writeFloat(v.fertility());
                buf.writeFloat(v.temperature()); buf.writeFloat(v.health());
                buf.writeString(v.pestType() == null ? "" : v.pestType());
            },
            buf -> new OpenSoilSensorPayload(
                    buf.readBlockPos(), buf.readFloat(), buf.readFloat(),
                    buf.readFloat(), buf.readFloat(),
                    buf.readString()
            )
    );

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
