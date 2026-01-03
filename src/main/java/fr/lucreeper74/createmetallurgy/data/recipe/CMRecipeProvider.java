package fr.lucreeper74.createmetallurgy.data.recipe;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import fr.lucreeper74.createmetallurgy.registries.CMTags.CMItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class CMRecipeProvider extends RecipeProvider {

    protected final List<GeneratedRecipe> all = new ArrayList<>();
    public static final int MELTING_DURATION = 40; // Duration in tick of the melting of an Ingot
    public static final int CASTING_DURATION = 60; // Duration in tick of the casting of an Ingot
    public static final int HEAT_CONDITION_THRESHOLD = 2000; // Threshold after which the condition is superheated

    public CMRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        all.forEach(c -> c.register(output));
        CreateMetallurgy.LOGGER
                .info(getName() + " registered " + all.size() + " recipe" + (all.size() == 1 ? "" : "s"));
    }

    protected GeneratedRecipe register(GeneratedRecipe recipe) {
        all.add(recipe);
        return recipe;
    }

    @FunctionalInterface
    public interface GeneratedRecipe {
        void register(RecipeOutput output);
    }

    public static class Marker {
    }

    // Shortcut for tags & items
    public static class T {
        /* Vanilla tags / Items */
        public static TagKey<Item> coal() {
            return Tags.Items.ORES_COAL;
        }

        /* Create tags / Items */
        public static TagKey<Item> sandpaper() {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "sand"));
        }

        public static ItemLike andesiteAlloy() {
            return AllItems.ANDESITE_ALLOY.get();
        }

        public static ItemLike andesiteCasing() {
            return AllBlocks.ANDESITE_CASING.get();
        }

        public static ItemLike copperCasing() {
            return AllBlocks.COPPER_CASING.get();
        }

        public static ItemLike shaft() {
            return AllBlocks.SHAFT.get();
        }

        public static ItemLike cog() {
            return AllBlocks.COGWHEEL.get();
        }

        /* Create Metallurgy tags / Items */
        public static ItemLike sandpaperBelt() {
            return CMItems.SANDPAPER_BELT.get();
        }

        public static ItemLike refractoryMortar() {
            return CMBlocks.REFRACTORY_MORTAR.get();
        }

        public static TagKey<Item> coke() {
            return CMItemTags.COAL_COKE.tag;
        }

        public static TagKey<Item> tungstenIngot() {
            return CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.INGOT);
        }

        public static TagKey<Item> tungstenSheet() {
            return CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.PLATE);
        }

        public static TagKey<Item> tungstenWire() {
            return CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.WIRE);
        }

        public static TagKey<Item> rawWolframite() {
            return CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.RAW_MATERIAL);
        }

        public static TagKey<Item> wolframiteBlock() {
            return CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.RAW_BLOCK);
        }

        public static TagKey<Item> obduriumIngot() {
            return CMMetals.OBDURIUM.getItemTag(CMMetals.ItemType.INGOT);
        }

        public static TagKey<Item> obduriumSheet() {
            return CMMetals.OBDURIUM.getItemTag(CMMetals.ItemType.PLATE);
        }

        public static TagKey<Item> steelIngot() {
            return CMMetals.STEEL.getItemTag(CMMetals.ItemType.INGOT);
        }

    }
}
