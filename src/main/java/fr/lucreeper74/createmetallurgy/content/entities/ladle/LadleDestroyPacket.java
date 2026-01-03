package fr.lucreeper74.createmetallurgy.content.entities.ladle;

import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LadleDestroyPacket(Vec3 position, ItemStack box) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<LadleDestroyPacket> TYPE = new CustomPacketPayload.Type<>(
            CreateMetallurgy.asResource("ladle_destroy"));

    // StreamCodec for Vec3 - serialize as three doubles
    private static final StreamCodec<RegistryFriendlyByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE,
            Vec3::x,
            ByteBufCodecs.DOUBLE,
            Vec3::y,
            ByteBufCodecs.DOUBLE,
            Vec3::z,
            Vec3::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, LadleDestroyPacket> STREAM_CODEC = StreamCodec.composite(
            VEC3_STREAM_CODEC,
            LadleDestroyPacket::position,
            ItemStack.STREAM_CODEC,
            LadleDestroyPacket::box,
            LadleDestroyPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LadleDestroyPacket packet, IPayloadContext context) {
        // Handle on client side for visual effects
        context.enqueueWork(() -> {
            // Client-side visual effects can be added here if needed
            // For now, the sound is handled server-side via
            // AllSoundEvents.PACKAGE_POP.playOnServer()
        });
    }
}
