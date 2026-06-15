package han.hanstudio.precisionAgriculture.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SetMachineRangePayload(BlockPos pos, int range, boolean isHarvester) implements CustomPayload {
    public static final Id<SetMachineRangePayload> ID = new Id<>(Identifier.of("precision-agriculture", "set_machine_range"));
    public static final PacketCodec<RegistryByteBuf, SetMachineRangePayload> CODEC = PacketCodec.of(
            (v, buf) -> { buf.writeBlockPos(v.pos()); buf.writeInt(v.range()); buf.writeBoolean(v.isHarvester()); },
            buf -> new SetMachineRangePayload(buf.readBlockPos(), buf.readInt(), buf.readBoolean())
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
