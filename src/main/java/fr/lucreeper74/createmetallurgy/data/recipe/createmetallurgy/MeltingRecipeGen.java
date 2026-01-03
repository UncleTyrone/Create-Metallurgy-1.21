package fr.lucreeper74.createmetallurgy.data.recipe.createmetallurgy;

import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;

import fr.lucreeper74.createmetallurgy.data.recipe.CMMetals;
import fr.lucreeper74.createmetallurgy.data.recipe.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.data.recipe.CMRecipeProvider;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;

import java.util.concurrent.CompletableFuture;

public class MeltingRecipeGen extends CMProcessingRecipesGen {

    GeneratedRecipe

    ALL_METALS = allMetals();
    //

    protected GeneratedRecipe allMetals() {
        for (CMMetals metal : CMMetals.values()) {
            for (CMMetals.ItemType type : CMMetals.ItemType.values()) {
                if (type.equals(CMMetals.ItemType.BLOCK) || type.equals(CMMetals.ItemType.RAW_BLOCK))
                    continue; // Skip blocks that can't be melted using Foundry Basin

                String recipeID = metal.getName() + "/" + type.getName();
                TagKey<Item> inputTag = metal.getItemTag(type);
                int duration = (int) (CMRecipeProvider.MELTING_DURATION * type.getDurationFactor());

                create(recipeID, b -> {
                    ProcessingRecipeBuilder<?, ?, ?> builder = b.duration(duration)
                            .withCondition(new NotCondition(new TagEmptyCondition(inputTag.location())));
                    ProcessingRecipeBuilder<?, ?, ?> builder2 = builder.require(inputTag)
                            .requiresHeat(metal.getMeltingPoint() <= HEAT_CONDITION_THRESHOLD ? HeatCondition.HEATED
                                    : HeatCondition.SUPERHEATED)
                            .output(metal.getFluid().get(), type.getFluidAmount());

                    if (type.isImpure())
                        builder2.output(CMFluids.MOLTEN_SLAG.get(), type.getImpurity());

                    return builder2;
                });
            }
        }
        return null;
    }

    //

    public MeltingRecipeGen(PackOutput generator, CompletableFuture<HolderLookup.Provider> registries) {
        super(generator, registries);
    }

    @Override
    protected CMRecipeTypes getRecipeType() {
        return CMRecipeTypes.MELTING;
    }
}
