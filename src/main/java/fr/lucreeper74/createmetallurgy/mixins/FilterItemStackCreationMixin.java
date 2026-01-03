package fr.lucreeper74.createmetallurgy.mixins;

import com.simibubi.create.content.logistics.filter.FilterItemStack;
import fr.lucreeper74.createmetallurgy.content.items.ladle_filter.LadleFilterItemStack;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

@Mixin(value = FilterItemStack.class, remap = false)
public abstract class FilterItemStackCreationMixin {
    private static Method trimFilterTagMethod;

    static {
        // Use reflection to find trimFilterTag method to avoid Mixin shadow warning
        try {
            trimFilterTagMethod = FilterItemStack.class.getDeclaredMethod("trimFilterTag", ItemStack.class);
            trimFilterTagMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            // Method doesn't exist in this version, will skip calling it
            trimFilterTagMethod = null;
        }
    }

    @Inject(method = "of(Lnet/minecraft/world/item/ItemStack;)Lcom/simibubi/create/content/logistics/filter/FilterItemStack;", at = @At("HEAD"), cancellable = true)
    private static void of(ItemStack filter, CallbackInfoReturnable<LadleFilterItemStack> info) {
        if (filter.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA) && CMItems.LADLE_FILTER.isIn(filter)) {
            // Call trimFilterTag via reflection if it exists
            if (trimFilterTagMethod != null) {
                try {
                    trimFilterTagMethod.invoke(null, filter);
                } catch (Exception e) {
                    // Method exists but failed to invoke, continue anyway
                }
            }
            info.setReturnValue(new LadleFilterItemStack(filter));
        }
    }
}