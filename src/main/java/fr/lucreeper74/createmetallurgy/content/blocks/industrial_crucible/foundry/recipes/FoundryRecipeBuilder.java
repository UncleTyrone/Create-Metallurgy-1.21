package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.resources.ResourceLocation;

/**
 * Builder for FoundryRecipe with heat requirements.
 * Uses Create's ProcessingRecipeBuilder internally.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class FoundryRecipeBuilder<T extends FoundryRecipe> extends ProcessingRecipeBuilder {

    public static final int DEFAULT_MIN_HEAT = -50;
    public static final int DEFAULT_MAX_HEAT = 50;

    protected int minHeatRequirement = DEFAULT_MIN_HEAT;
    protected int maxHeatRequirement = DEFAULT_MAX_HEAT;

    public FoundryRecipeBuilder(Object factory, ResourceLocation recipeId) {
        // ProcessingRecipeBuilder constructor in Create 6.0 takes:
        // ProcessingRecipe.Factory<ProcessingRecipeParams, ProcessingRecipe> factory,
        // ResourceLocation id
        // The factory from serializer.getFactory() returns ProcessingRecipe.Factory
        // When extending raw ProcessingRecipeBuilder, cast to raw Factory type
        super((ProcessingRecipe.Factory<?, ?>) factory, recipeId);
    }

    @Override
    protected ProcessingRecipeParams createParams() {
        // ProcessingRecipeParams constructor is package-private, use reflection to
        // access it
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

    public FoundryRecipeBuilder<T> requiresMinHeat(int minHeat) {
        minHeatRequirement = minHeat;
        return this;
    }

    public FoundryRecipeBuilder<T> requiresMaxHeat(int maxHeat) {
        maxHeatRequirement = maxHeat;
        return this;
    }

    public int getMinHeatRequirement() {
        return minHeatRequirement;
    }

    public int getMaxHeatRequirement() {
        return maxHeatRequirement;
    }

    @Override
    public T build() {
        T recipe = (T) super.build();
        recipe.withHeatRange(minHeatRequirement, maxHeatRequirement);
        return recipe;
    }
}
