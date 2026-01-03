package fr.lucreeper74.createmetallurgy.data.recipe.create;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.util.entry.ItemEntry;

import fr.lucreeper74.createmetallurgy.data.recipe.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class CMWashingRecipeGen extends CMProcessingRecipesGen {

    GeneratedRecipe

    COPPER_DUST = dirtyDust(CMItems.DIRTY_COPPER_DUST, CMItems.COPPER_DUST::get, () -> Items.CLAY_BALL, .5f),
            GOLD_DUST = dirtyDust(CMItems.DIRTY_GOLD_DUST, CMItems.GOLD_DUST::get, () -> Items.QUARTZ, .5f),
            IRON_DUST = dirtyDust(CMItems.DIRTY_IRON_DUST, CMItems.IRON_DUST::get, () -> Items.REDSTONE, .5f),
            ZINC_DUST = dirtyDust(CMItems.DIRTY_ZINC_DUST, CMItems.ZINC_DUST::get, () -> Items.GUNPOWDER, .5f),
            WOLFRAMITE_DUST = dirtyDust(CMItems.DIRTY_WOLFRAMITE_DUST, CMItems.WOLFRAMITE_DUST::get,
                    () -> Items.GOLD_NUGGET, .5f);

    //

    public GeneratedRecipe dirtyDust(ItemEntry<TagDependentIngredientItem> dirtyDust, Supplier<ItemLike> dust,
            Supplier<ItemLike> secondary,
            float secondaryChance) {
        return create(dirtyDust::get, b -> b.output(dust.get())
                .output(secondaryChance, secondary.get()));
    }

    //

    public CMWashingRecipeGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    @Override
    protected AllRecipeTypes getRecipeType() {
        return AllRecipeTypes.SPLASHING;
    }
}
