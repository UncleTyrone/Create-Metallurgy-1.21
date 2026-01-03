package fr.lucreeper74.createmetallurgy.data.recipe.create;

import com.simibubi.create.AllRecipeTypes;

import fr.lucreeper74.createmetallurgy.data.recipe.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class CMPressingRecipeGen extends CMProcessingRecipesGen {

    GeneratedRecipe

    GRAPHITE = create(CMItems.GRAPHITE::get, b -> b.output(CMItems.GRAPHITE_BLANK_MOLD.get()));

    //

    public CMPressingRecipeGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.PRESSING;
    }
}