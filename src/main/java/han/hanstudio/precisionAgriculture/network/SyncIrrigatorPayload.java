package han.hanstudio.precisionAgriculture.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public record SyncIrrigatorPayload(List<BlockPos> positions) implements CustomPayload {

    public static final Id<SyncIrrigatorPayload> ID = new Id<>(Identifier.of("precision-agriculture", "sync_irrigator"));

    public static final PacketCodec<RegistryByteBuf, SyncIrrigatorPayload> CODEC = PacketCodec.of(
            (v, buf) -> {
                buf.writeInt(v.positions().size());
                for (BlockPos p : v.positions()) buf.writeBlockPos(p);
            },
            buf -> {
                int size = buf.readInt();
                List<BlockPos> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) list.add(buf.readBlockPos());
                return new SyncIrrigatorPayload(list);
            }
    );

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
