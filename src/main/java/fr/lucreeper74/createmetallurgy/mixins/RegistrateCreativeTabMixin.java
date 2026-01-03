package fr.lucreeper74.createmetallurgy.mixins;

import com.tterrag.registrate.AbstractRegistrate;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractRegistrate.class, remap = false)
public abstract class RegistrateCreativeTabMixin {

    @Shadow(remap = false)
    private String modid;

    // Cancel Registrate's event listener for our creative tab
    // We handle it ourselves via DisplayItemsGenerator
    @Inject(method = "onBuildCreativeModeTabContents", at = @At("HEAD"), cancellable = true, remap = false)
    private void preventCreativeTabAdditions(BuildCreativeModeTabContentsEvent event, CallbackInfo ci) {
        // Only cancel for our own Registrate instance (check by mod ID)
        if (!CreateMetallurgy.MOD_ID.equals(this.modid)) {
            return; // Not our Registrate, let it proceed normally
        }

        // Compare by checking if the tab's ResourceLocation matches our tab's location
        // This is more reliable than object comparison
        try {
            ResourceLocation tabLocation = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(event.getTab());
            if (tabLocation != null && tabLocation.equals(CreateMetallurgy.asResource("main_group"))) {
                CreateMetallurgy.LOGGER.info("[RegistrateCreativeTabMixin] Canceling Registrate's creative tab additions for tab: {}", tabLocation);
                ci.cancel(); // Prevent Registrate from adding items to our tab
                return;
            }
        } catch (Exception e) {
            // If comparison fails for any reason, don't cancel (fail-safe)
            CreateMetallurgy.LOGGER.warn("[RegistrateCreativeTabMixin] Error checking tab location: {}", e.getMessage());
        }
    }

}
