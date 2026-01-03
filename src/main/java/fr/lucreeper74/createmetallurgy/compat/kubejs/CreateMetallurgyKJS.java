package fr.lucreeper74.createmetallurgy.compat.kubejs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import fr.lucreeper74.createmetallurgy.compat.kubejs.recipes.*;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;

import java.util.Map;

public class CreateMetallurgyKJS implements KubeJSPlugin {

    private static final Map<CMRecipeTypes, RecipeSchema> recipeSchemas = Map.of(
            CMRecipeTypes.CASTING_IN_BASIN, CastingRecipeJS.SCHEMA,
            CMRecipeTypes.CASTING_IN_TABLE, CastingRecipeJS.SCHEMA,
            CMRecipeTypes.GRINDING, ProcessingRecipeJS.BASIC,
            CMRecipeTypes.ALLOYING, ProcessingRecipeJS.WITH_HEAT,
            CMRecipeTypes.MELTING, ProcessingRecipeJS.WITH_HEAT);

    public void onRegisterRecipeSchemas(KubeJSContext cx, RecipeSchemaRegistry registry) {

        for (CMRecipeTypes recipeType : recipeSchemas.keySet()) {
            RecipeSchema schema = recipeSchemas.get(recipeType);
            registry.register(recipeType.getId(), schema);
        }
    }
}