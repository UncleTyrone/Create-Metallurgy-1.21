package fr.lucreeper74.createmetallurgy.content.items.ladle_filter;

import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LadleFilterScreenPacket(Type packetType, CompoundTag data) implements CustomPacketPayload {

    public enum Type {
        UPDATE_ADDRESS, UPDATE_PERCENT, UPDATE_FLUID
    }

    public static final CustomPacketPayload.Type<LadleFilterScreenPacket> TYPE = new CustomPacketPayload.Type<>(
            CreateMetallurgy.asResource("ladle_filter_screen"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LadleFilterScreenPacket> STREAM_CODEC = StreamCodec
            .composite(
                    ByteBufCodecs.VAR_INT.<RegistryFriendlyByteBuf>cast().map(i -> Type.values()[i], Type::ordinal),
                    LadleFilterScreenPacket::packetType,
                    ByteBufCodecs.COMPOUND_TAG.<RegistryFriendlyByteBuf>cast(),
                    LadleFilterScreenPacket::data,
                    LadleFilterScreenPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LadleFilterScreenPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player))
                return;

            if (player.containerMenu instanceof LadleFilterMenu menu) {
                switch (packet.packetType()) {
                    case UPDATE_ADDRESS -> {
                        menu.address = packet.data().getString("Address");
                    }
                    case UPDATE_PERCENT -> {
                        menu.filledAmount = packet.data().getInt("FilledAmount");
                        menu.comparator = packet.data().getInt("Comparator");
                    }
                    case UPDATE_FLUID -> {
                        menu.fluidFilter = FluidStack.parseOptional(player.registryAccess(),
                                packet.data().getCompound("FluidFilter"));
                    }
                }
            }
        });
    }
}
