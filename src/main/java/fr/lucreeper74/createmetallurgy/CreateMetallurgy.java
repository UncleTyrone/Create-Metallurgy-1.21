package fr.lucreeper74.createmetallurgy;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.CastingWithSpout;
import fr.lucreeper74.createmetallurgy.content.blocks.light_bulb.network.NetworkHandler;
import fr.lucreeper74.createmetallurgy.data.CMDatagen;
import fr.lucreeper74.createmetallurgy.registries.*;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(CreateMetallurgy.MOD_ID)
public class CreateMetallurgy {

    public static final String MOD_ID = "createmetallurgy";

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final NetworkHandler NETWORK_HANDLER = new NetworkHandler();

    static {
        REGISTRATE.setTooltipModifierFactory(
                item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                        .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
        // Explicitly set creative tab to null to prevent Registrate from automatically
        // adding items to tabs. We use a custom DisplayItemsGenerator instead.
        REGISTRATE.setCreativeTab(null);
    }

    public CreateMetallurgy(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            CMPartialModels.init();
        }

        CMCreativeTabs.register(modEventBus);
        CMDisplaySources.register();
        CMBlocks.register();
        CMItems.register();
        CMFluids.register();
        CMMenuTypes.register();
        CMEntityTypes.register();
        CMSpriteShifts.init();
        CMBlockEntityTypes.register();
        CMRecipeTypes.register(modEventBus);
        CMPackets.registerPackets(modEventBus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            CreateMetallurgyClient.loadClient(modEventBus);
        }

        modEventBus.addListener(CreateMetallurgy::init);
        modEventBus.addListener(CreateMetallurgy::onRegister);
        modEventBus.addListener(CMEntityTypes::registerEntityAttributes);
        modEventBus.addListener(EventPriority.LOWEST, CMDatagen::gatherData);
    }

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CastingWithSpout.registerDefaults();
        });
    }

    public static void onRegister(final RegisterEvent event) {
        CMArmInteract.init();
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    // Keep deprecated method for compatibility during migration
    @Deprecated
    public static ResourceLocation genRL(String path) {
        return asResource(path);
    }
}
