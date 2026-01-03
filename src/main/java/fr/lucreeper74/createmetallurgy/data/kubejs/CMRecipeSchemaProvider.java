package fr.lucreeper74.createmetallurgy.data.kubejs;

import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.RecipeSchemaProvider;
import fr.lucreeper74.createmetallurgy.compat.kubejs.recipes.CastingRecipeJS;
import fr.lucreeper74.createmetallurgy.compat.kubejs.recipes.ProcessingRecipeJS;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Map;

public class CMRecipeSchemaProvider extends RecipeSchemaProvider {

    private static final Map<CMRecipeTypes, RecipeSchema> recipeSchemas = Map.of(
            CMRecipeTypes.CASTING_IN_BASIN, CastingRecipeJS.SCHEMA,
            CMRecipeTypes.CASTING_IN_TABLE, CastingRecipeJS.SCHEMA,
            CMRecipeTypes.GRINDING, ProcessingRecipeJS.BASIC,
            CMRecipeTypes.ALLOYING, ProcessingRecipeJS.WITH_HEAT,
            CMRecipeTypes.MELTING, ProcessingRecipeJS.WITH_HEAT);

    public CMRecipeSchemaProvider(String name, GatherDataEvent event) {
        super(name, event);
    }

    @Override
    public void add(HolderLookup.Provider lookup) {
        for (Map.Entry<CMRecipeTypes, RecipeSchema> entry : recipeSchemas.entrySet()) {
            CMRecipeTypes recipeType = entry.getKey();
            ResourceLocation recipeId = recipeType.getId();
            add(recipeId, builder -> {
                // Schema is already defined and registered via CreateMetallurgyKJS plugin
                // The builder is used for additional configuration like mappings
            });
        }
    }
}
