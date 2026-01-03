package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import fr.lucreeper74.createmetallurgy.content.processing.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.belt_grinder.GrindingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_lid.MeltingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.foundry_mixer.AlloyingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe.CastingRecipeSerializer;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.BulkMeltingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.EntityMeltingRecipe;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import java.util.function.Supplier;

public enum CMRecipeTypes implements IRecipeTypeInfo {

    MELTING(createFactory(MeltingRecipe::new)),
    ALLOYING(createFactory(AlloyingRecipe::new)),
    GRINDING(createGrindingFactory()),
    BULK_MELTING(createFactory(BulkMeltingRecipe::new)),
    ENTITY_MELTING(createFactory(EntityMeltingRecipe::new)),

    CASTING_IN_BASIN(CastingRecipeSerializer.CastingBasinRecipeSerializer::new),
    CASTING_IN_TABLE(CastingRecipeSerializer.CastingTableRecipeSerializer::new);

    private final ResourceLocation id;
    private final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializerObject;
    private final Supplier<RecipeType<?>> type;

    CMRecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = CMLang.asId(name());
        id = CreateMetallurgy.asResource(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        @Nullable
        DeferredHolder<RecipeType<?>, RecipeType<?>> typeObject = Registers.TYPE_REGISTER.register(name,
                () -> RecipeType.simple(id));
        type = typeObject;
    }

    CMRecipeTypes(ProcessingRecipe.Factory<ProcessingRecipeParams, ?> processingFactory) {
        // processingFactory is ProcessingRecipe.Factory<ProcessingRecipeParams, T>
        // which takes ProcessingRecipeParams and returns a ProcessingRecipe
        this(() -> new ProcessingRecipeSerializer<>((Object) processingFactory));
    }

    private static <T extends ProcessingRecipe<net.minecraft.world.item.crafting.RecipeInput, ProcessingRecipeParams>> ProcessingRecipe.Factory<ProcessingRecipeParams, T> createFactory(
            java.util.function.Function<ProcessingRecipeParams, T> function) {
        // Wrap a Function as a ProcessingRecipe.Factory
        return params -> function.apply(params);
    }

    private static ProcessingRecipe.Factory<ProcessingRecipeParams, GrindingRecipe> createGrindingFactory() {
        // Return a factory that will capture GRINDING when it's actually used
        // The factory will be created lazily when the serializer is first accessed
        return new ProcessingRecipe.Factory<ProcessingRecipeParams, GrindingRecipe>() {
            private ProcessingRecipe.Factory<ProcessingRecipeParams, GrindingRecipe> delegate;

            @Override
            public GrindingRecipe create(ProcessingRecipeParams params) {
                if (delegate == null) {
                    // Now GRINDING is available, create the real factory
                    delegate = createFactory(params2 -> new GrindingRecipe(GRINDING, params2));
                }
                return delegate.create(params);
            }
        };
    }

    public static void register(IEventBus modEventBus) {
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) serializerObject.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeType<?> getType() {
        // IRecipeTypeInfo expects RecipeType<Recipe> but we return RecipeType<?>
        // This is safe because RecipeType<?> is compatible with RecipeType<Recipe>
        return (RecipeType<?>) type.get();
    }

    private static class Registers {
        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister
                .create(Registries.RECIPE_SERIALIZER, CreateMetallurgy.MOD_ID);
        private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister
                .create(Registries.RECIPE_TYPE, CreateMetallurgy.MOD_ID);
    }
}
