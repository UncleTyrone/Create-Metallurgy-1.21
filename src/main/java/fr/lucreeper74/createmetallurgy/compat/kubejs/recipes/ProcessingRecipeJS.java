package fr.lucreeper74.createmetallurgy.compat.kubejs.recipes;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.FluidIngredientComponent;
import dev.latvian.mods.kubejs.recipe.component.FluidStackComponent;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface ProcessingRecipeJS {

    RecipeKey<?> OUTPUT = FluidStackComponent.FLUID_STACK.key("results", ComponentRole.OUTPUT);
    RecipeKey<?> INPUT = FluidIngredientComponent.FLUID_INGREDIENT.key("ingredients", ComponentRole.INPUT);
    RecipeKey<Double> TIME = NumberComponent.DOUBLE.key("processingTime", ComponentRole.OTHER).optional(100d);

    RecipeKey<String> HEAT = StringComponent.STRING
            .key("heatRequirement", ComponentRole.OTHER)
            .defaultOptional();

    RecipeSchema WITH_HEAT = new RecipeSchema(OUTPUT, INPUT, TIME, HEAT);
    RecipeSchema BASIC = new RecipeSchema(OUTPUT, INPUT, TIME);
}
