package fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe;

import com.simibubi.create.foundation.data.SimpleDatagenIngredient;
import com.simibubi.create.foundation.data.recipe.Mods;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import com.tterrag.registrate.util.DataIngredient;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.ArrayList;
import java.util.List;

public class CastingRecipeBuilder {

    private CastingRecipe recipe;
    protected List<ICondition> recipeConditions;

    public CastingRecipeBuilder(CMRecipeTypes type, ResourceLocation id) {
        switch (type) {
            case CASTING_IN_TABLE -> this.recipe = new CastingTableRecipe(id);
            case CASTING_IN_BASIN -> this.recipe = new CastingBasinRecipe(id);
            default -> throw new IllegalArgumentException("Recipe type '" + type + "' its not a Casting Recipe");
        }
        recipeConditions = new ArrayList<>();
    }

    // For Inputs
    public CastingRecipeBuilder require(TagKey<Item> tag) {
        return require(Ingredient.of(tag));
    }

    public CastingRecipeBuilder require(ItemLike item) {
        return require(Ingredient.of(item));
    }

    public CastingRecipeBuilder require(Ingredient ingredient) {
        recipe.ingredient = ingredient;
        return this;
    }

    public CastingRecipeBuilder require(Mods mod, String id) {
        // SimpleDatagenIngredient is compatible with Ingredient at runtime
        recipe.ingredient = (Ingredient) (Object) new SimpleDatagenIngredient(mod, id);
        return this;
    }

    public CastingRecipeBuilder require(ResourceLocation ingredient) {
        // DataIngredient is compatible with Ingredient at runtime
        recipe.ingredient = (Ingredient) (Object) DataIngredient.ingredient(null, ingredient);
        return this;
    }

    public CastingRecipeBuilder require(Fluid fluid, int amount) {
        return require(SizedFluidIngredient.of(new FluidStack(fluid, amount)));
    }

    public CastingRecipeBuilder require(TagKey<Fluid> fluidTag, int amount) {
        return require(SizedFluidIngredient.of(fluidTag, amount));
    }

    public CastingRecipeBuilder require(SizedFluidIngredient ingredient) {
        recipe.fluidIngredient = ingredient;
        return this;
    }

    // For Output ItemStack
    public CastingRecipeBuilder output(ItemLike item) {
        return output(item, 1);
    }

    public CastingRecipeBuilder output(ItemLike item, int amount) {
        return output(new ItemStack(item, amount));
    }

    public CastingRecipeBuilder output(ItemStack output) {
        return output(CastingOutput.fromStack(output));
    }

    // For Output Tags
    public CastingRecipeBuilder output(TagKey<Item> tag) {
        return output(tag, 1);
    }

    public CastingRecipeBuilder output(TagKey<Item> tag, int amount) {
        return output(CastingOutput.fromTag(tag, amount));
    }

    public CastingRecipeBuilder output(CastingOutput output) {
        recipe.result = output;
        return this;
    }

    // Others
    public CastingRecipeBuilder duration(int ticks) {
        recipe.processingDuration = ticks;
        return this;
    }

    public CastingRecipeBuilder withMoldConsumed(boolean condition) {
        recipe.moldConsumed = condition;
        return this;
    }

    public CastingRecipeBuilder whenModLoaded(String modid) {
        return withCondition(new ModLoadedCondition(modid));
    }

    public CastingRecipeBuilder withCondition(ICondition condition) {
        recipeConditions.add(condition);
        return this;
    }

    // Build Datagen
    public CastingRecipe build() {
        return recipe;
    }

    // Build method for RecipeOutput (Minecraft 1.21)
    public void build(RecipeOutput output, HolderLookup.Provider registries) {
        CastingRecipe builtRecipe = build();
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(builtRecipe.getId().getNamespace(),
                builtRecipe.getTypeInfo().getId().getPath() + "/" + builtRecipe.getId().getPath());

        // Use RecipeOutput to accept the recipe
        // Note: Conditions are not directly supported in RecipeOutput.accept()
        // They would need to be handled via a custom RecipeOutput wrapper or serializer
        // modification
        output.accept(id, builtRecipe, null);
    }
}
