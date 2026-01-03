package fr.lucreeper74.createmetallurgy.data.recipe.create;

import com.simibubi.create.AllRecipeTypes;

import fr.lucreeper74.createmetallurgy.data.recipe.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class CMMixingRecipeGen extends CMProcessingRecipesGen {

    GeneratedRecipe

    REFRACTORY_MORTAR = create("refractory_mortar", b -> b.require(Tags.Items.SANDS)
            .require(Tags.Items.SANDS)
            .require(Items.CLAY_BALL)
            .require(Fluids.WATER, 100)
            .output(CMBlocks.REFRACTORY_MORTAR.get()));

    //

    public CMMixingRecipeGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.MIXING;
    }
}
