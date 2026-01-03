package fr.lucreeper74.createmetallurgy.data.recipe.createmetallurgy;

import fr.lucreeper74.createmetallurgy.data.recipe.CMProcessingRecipesGen;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class GrindingRecipeGen extends CMProcessingRecipesGen {

    GeneratedRecipe

    ALL_COPPER_BLOCKS = deoxidized(),
            ALL_WAXED_COPPER_BLOCKS = unwaxed()

    ;

    //

    protected GeneratedRecipe deoxidized() {
        // Iterate through all blocks and find weathering copper blocks
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof WeatheringCopper) {
                var nextOpt = ((WeatheringCopper) block).getNext(block.defaultBlockState());
                if (nextOpt.isPresent()) {
                    Block current = nextOpt.get().getBlock();
                    var previousOpt = WeatheringCopper.getPrevious(current.defaultBlockState());
                    if (previousOpt.isPresent()) {
                        Block previous = previousOpt.get().getBlock();
                        create(BuiltInRegistries.BLOCK.getKey(current).getPath(), b -> b.duration(50)
                                .require(current)
                                .output(previous));
                    }
                }
            }
        }
        return null;
    }

    protected GeneratedRecipe unwaxed() {
        Set<Block> coppers = new HashSet<>();
        // Collect all weathering copper blocks
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof WeatheringCopper) {
                coppers.add(block);
            }
        }

        for (Block normal : coppers) {
            ResourceLocation normalKey = BuiltInRegistries.BLOCK.getKey(normal);
            String waxedName = "waxed_" + normalKey.getPath();
            ResourceLocation waxedKey = ResourceLocation.fromNamespaceAndPath(normalKey.getNamespace(), waxedName);
            Block waxed = BuiltInRegistries.BLOCK.get(waxedKey);

            if (waxed != null) {
                create(waxedName, b -> b.duration(50)
                        .require(waxed)
                        .output(normal));
            }
        }
        return null;
    }

    //

    public GrindingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected CMRecipeTypes getRecipeType() {
        return CMRecipeTypes.GRINDING;
    }
}
