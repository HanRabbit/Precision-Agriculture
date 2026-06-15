package han.hanstudio.precisionAgriculture.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SyncMachineStatusPayload(BlockPos pos, int range, int count, boolean isHarvester) implements CustomPayload {
    public static final Id<SyncMachineStatusPayload> ID = new Id<>(Identifier.of("precision-agriculture", "sync_machine_status"));
    public static final PacketCodec<RegistryByteBuf, SyncMachineStatusPayload> CODEC = PacketCodec.of(
            (v, buf) -> { buf.writeBlockPos(v.pos()); buf.writeInt(v.range()); buf.writeInt(v.count()); buf.writeBoolean(v.isHarvester()); },
            buf -> new SyncMachineStatusPayload(buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readBoolean())
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
