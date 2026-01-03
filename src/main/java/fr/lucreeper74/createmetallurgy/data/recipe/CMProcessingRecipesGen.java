package fr.lucreeper74.createmetallurgy.data.recipe;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.data.recipe.create.*;
import fr.lucreeper74.createmetallurgy.data.recipe.createmetallurgy.AlloyingRecipeGen;
import fr.lucreeper74.createmetallurgy.data.recipe.createmetallurgy.GrindingRecipeGen;
import fr.lucreeper74.createmetallurgy.data.recipe.createmetallurgy.MeltingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public abstract class CMProcessingRecipesGen extends CMRecipeProvider {
    protected static final List<CMProcessingRecipesGen> GENS = new ArrayList<>();

    public static void registerAll(DataGenerator gen, PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries) {
        GENS.add(new GrindingRecipeGen(output, registries));
        GENS.add(new MeltingRecipeGen(output, registries));
        GENS.add(new AlloyingRecipeGen(output, registries));

        /* Create Recipes */
        GENS.add(new CMMixingRecipeGen(output, registries));
        GENS.add(new CMCrushingRecipeGen(output, registries));
        GENS.add(new CMMillingRecipeGen(output, registries));
        GENS.add(new CMWashingRecipeGen(output, registries));
        GENS.add(new CMPressingRecipeGen(output, registries));
        GENS.add(new CMCompactingRecipeGen(output, registries));

        gen.addProvider(true, new DataProvider() {

            @Override
            public String getName() {
                return "Create: Metallurgy's Processing Recipes";
            }

            @Override
            public CompletableFuture<?> run(CachedOutput dc) {
                return CompletableFuture.allOf(GENS.stream()
                        .map(gen -> gen.run(dc))
                        .toArray(CompletableFuture[]::new));
            }
        });
    }

    public CMProcessingRecipesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    protected GeneratedRecipe create(Supplier<ItemLike> singleIngredient,
            UnaryOperator<ProcessingRecipeBuilder<?, ?, ?>> transform) {
        return create(CreateMetallurgy.MOD_ID, singleIngredient, transform);
    }

    protected GeneratedRecipe create(String name,
            UnaryOperator<ProcessingRecipeBuilder<?, ?, ?>> transform) {
        return create(CreateMetallurgy.asResource(name), transform);
    }

    protected GeneratedRecipe create(ResourceLocation name,
            UnaryOperator<ProcessingRecipeBuilder<?, ?, ?>> transform) {
        return createWithDeferredId(() -> name, transform);
    }

    /**
     * Recipe with recipe name provided by the function
     */
    protected GeneratedRecipe createWithDeferredId(Supplier<ResourceLocation> name,
            UnaryOperator<ProcessingRecipeBuilder<?, ?, ?>> transform) {
        RecipeSerializer<?> serializer = getRecipeType().getSerializer();
        ProcessingRecipe.Factory<?, ?> factory = getFactoryFromSerializer(serializer);
        GeneratedRecipe generatedRecipe = c -> transform
                .apply(createBuilder(factory, name.get()))
                .build(c);
        all.add(generatedRecipe);
        return generatedRecipe;
    }

    /**
     * Recipe with single ingredient (its name as recipe name)
     */
    protected GeneratedRecipe create(String namespace,
            Supplier<ItemLike> singleIngredient, UnaryOperator<ProcessingRecipeBuilder<?, ?, ?>> transform) {
        RecipeSerializer<?> serializer = getRecipeType().getSerializer();
        ProcessingRecipe.Factory<?, ?> factory = getFactoryFromSerializer(serializer);
        GeneratedRecipe generatedRecipe = c -> {
            ItemLike itemLike = singleIngredient.get();
            transform
                    .apply(createBuilder(factory,
                            ResourceLocation.fromNamespaceAndPath(namespace,
                                    BuiltInRegistries.ITEM.getKey(itemLike.asItem())
                                            .getPath()))
                            .withItemIngredients(Ingredient.of(itemLike)))
                    .build(c);
        };
        all.add(generatedRecipe);
        return generatedRecipe;
    }

    protected abstract IRecipeTypeInfo getRecipeType();

    private ProcessingRecipe.Factory<?, ?> getFactoryFromSerializer(RecipeSerializer<?> serializer) {
        try {
            return (ProcessingRecipe.Factory<?, ?>) serializer.getClass().getMethod("getFactory").invoke(serializer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get factory from serializer", e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private ProcessingRecipeBuilder<?, ?, ?> createBuilder(ProcessingRecipe.Factory<?, ?> factory,
            ResourceLocation id) {
        // ProcessingRecipeBuilder is abstract and requires createParams() and self()
        // implementation
        return new ProcessingRecipeBuilder(factory, id) {
            @Override
            protected ProcessingRecipeParams createParams() {
                try {
                    java.lang.reflect.Constructor<ProcessingRecipeParams> constructor = ProcessingRecipeParams.class
                            .getDeclaredConstructor();
                    constructor.setAccessible(true);
                    return constructor.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create ProcessingRecipeParams", e);
                }
            }

            @Override
            public ProcessingRecipeBuilder self() {
                return this;
            }
        };
    }
}