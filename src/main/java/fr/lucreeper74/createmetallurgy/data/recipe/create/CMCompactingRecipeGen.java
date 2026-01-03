package fr.lucreeper74.createmetallurgy.data.recipe.create;

import com.simibubi.create.AllRecipeTypes;

import fr.lucreeper74.createmetallurgy.data.recipe.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class CMCompactingRecipeGen extends CMProcessingRecipesGen {

    GeneratedRecipe

    TUFF = create("tuff_from_slag", b -> b.require(CMItems.SLAG.get())
            .require(CMItems.SLAG.get())
            .require(CMItems.SLAG.get())
            .require(CMItems.SLAG.get())
            .require(Items.GRAVEL)
            .require(Items.COBBLESTONE)
            .output(Blocks.TUFF, 1));

    //

    public CMCompactingRecipeGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.COMPACTING;
    }
}