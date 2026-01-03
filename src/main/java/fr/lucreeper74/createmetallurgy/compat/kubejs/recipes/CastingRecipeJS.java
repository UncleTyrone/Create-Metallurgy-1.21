package fr.lucreeper74.createmetallurgy.compat.kubejs.recipes;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.FluidIngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemStackComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface CastingRecipeJS {
    RecipeKey<?> OUTPUT = ItemStackComponent.ITEM_STACK.key("result", ComponentRole.OUTPUT);
    RecipeKey<?> INPUT = FluidIngredientComponent.FLUID_INGREDIENT.key("ingredients", ComponentRole.INPUT);
    RecipeKey<Double> TIME = NumberComponent.DOUBLE.key("processingTime", ComponentRole.OTHER).optional(100d);
    RecipeKey<Boolean> CAST = BooleanComponent.BOOLEAN.key("mold_consumed", ComponentRole.OTHER).optional(false);

    RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT, TIME, CAST);
}