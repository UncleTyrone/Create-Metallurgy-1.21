package fr.lucreeper74.createmetallurgy.registries;

import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleDestroyPacket;
import fr.lucreeper74.createmetallurgy.content.items.ladle_filter.LadleFilterScreenPacket;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class CMPackets {

    public static void registerPackets(IEventBus modEventBus) {
        modEventBus.addListener(CMPackets::registerPayloads);
    }

    private static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(CreateMetallurgy.MOD_ID)
                .versioned("1");

        // Client to Server packets
        registrar.playToServer(
                LadleFilterScreenPacket.TYPE,
                LadleFilterScreenPacket.STREAM_CODEC,
                LadleFilterScreenPacket::handle
        );

        // Server to Client packets
        registrar.playToClient(
                LadleDestroyPacket.TYPE,
                LadleDestroyPacket.STREAM_CODEC,
                LadleDestroyPacket::handle
        );
    }
}
