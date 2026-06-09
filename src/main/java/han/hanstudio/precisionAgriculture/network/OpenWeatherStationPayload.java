package han.hanstudio.precisionAgriculture.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OpenWeatherStationPayload(float temperature, int lightLevel, boolean raining) implements CustomPayload {

    public static final Id<OpenWeatherStationPayload> ID = new Id<>(Identifier.of("precision-agriculture", "open_weather_station"));

    public static final PacketCodec<net.minecraft.network.RegistryByteBuf, OpenWeatherStationPayload> CODEC = PacketCodec.of(
            (v, buf) -> { buf.writeFloat(v.temperature()); buf.writeInt(v.lightLevel()); buf.writeBoolean(v.raining()); },
            buf -> new OpenWeatherStationPayload(buf.readFloat(), buf.readInt(), buf.readBoolean())
    );

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
