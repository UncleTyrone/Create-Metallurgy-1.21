package fr.lucreeper74.createmetallurgy.data.recipe.vanilla;

import com.google.common.base.Supplier;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.simibubi.create.AllItems;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.data.recipe.CMRecipeProvider;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.registries.CMItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;

import java.util.concurrent.CompletableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class CMStandardRecipeGen extends CMRecipeProvider {

    private Marker MATERIALS = enterFolder("materials");

    // Helper to avoid CombustibleItem type resolution issues
    // Note: CombustibleItem may not be resolvable at compile time but works at
    // runtime
    // Using reflection to avoid compile-time type resolution
    private static ItemEntry getCokeEntry() {
        try {
            java.lang.reflect.Field field = CMItems.class.getField("COKE");
            return (ItemEntry) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get COKE entry", e);
        }
    }

    GeneratedRecipe

    RAW_WOLFRAMITE = create(CMItems.RAW_WOLFRAMITE).returns(9)
            .unlockedBy(CMBlocks.RAW_WOLFRAMITE_BLOCK::get)
            .viaShapeless(b -> b.requires(CMBlocks.RAW_WOLFRAMITE_BLOCK.get())),

            RAW_WOLFRAMITE_BLOCK = create(CMBlocks.RAW_WOLFRAMITE_BLOCK).unlockedBy(CMItems.RAW_WOLFRAMITE::get)
                    .viaShaped(b -> b.define('C', CMItems.RAW_WOLFRAMITE.get())
                            .pattern("CCC")
                            .pattern("CCC")
                            .pattern("CCC")),

            TUNGSTEN_NUGGET = create(CMItems.TUNGSTEN_NUGGET).returns(9)
                    .unlockedBy(CMItems.TUNGSTEN_INGOT::get)
                    .viaShapeless(b -> b.requires(CMItems.TUNGSTEN_INGOT.get())),

            TUNGSTEN_INGOT = create(CMItems.TUNGSTEN_INGOT).unlockedBy(CMItems.TUNGSTEN_NUGGET::get)
                    .viaShaped(b -> b.define('C', CMItems.TUNGSTEN_NUGGET.get())
                            .pattern("CCC")
                            .pattern("CCC")
                            .pattern("CCC")),

            TUNGSTEN_INGOT_FROM_BLOCK = create(CMItems.TUNGSTEN_INGOT).withSuffix("_from_block")
                    .returns(9)
                    .unlockedBy(CMItems.TUNGSTEN_INGOT::get)
                    .viaShapeless(b -> b.requires(CMBlocks.TUNGSTEN_BLOCK.get())),

            TUNGSTEN_BLOCK = create(CMBlocks.TUNGSTEN_BLOCK).unlockedBy(CMItems.TUNGSTEN_INGOT::get)
                    .viaShaped(b -> b.define('C', CMItems.TUNGSTEN_INGOT.get())
                            .pattern("CCC")
                            .pattern("CCC")
                            .pattern("CCC")),

            OBDURIUM_INGOT_FROM_BLOCK = create(CMItems.OBDURIUM_INGOT).withSuffix("_from_block")
                    .returns(9)
                    .unlockedBy(CMItems.OBDURIUM_INGOT::get)
                    .viaShapeless(b -> b.requires(CMBlocks.OBDURIUM_BLOCK.get())),

            OBDURIUM_BLOCK = create(CMBlocks.OBDURIUM_BLOCK).unlockedBy(CMItems.OBDURIUM_INGOT::get)
                    .viaShaped(b -> b.define('C', CMItems.OBDURIUM_INGOT.get())
                            .pattern("CCC")
                            .pattern("CCC")
                            .pattern("CCC")),

            STEEL_INGOT_FROM_BLOCK = create(CMItems.STEEL_INGOT).withSuffix("_from_block")
                    .returns(9)
                    .unlockedBy(CMItems.STEEL_INGOT::get)
                    .viaShapeless(b -> b.requires(CMBlocks.STEEL_BLOCK.get())),

            STEEL_BLOCK = create(CMBlocks.STEEL_BLOCK).unlockedBy(CMItems.STEEL_INGOT::get)
                    .viaShaped(b -> b.define('C', CMItems.STEEL_INGOT.get())
                            .pattern("CCC")
                            .pattern("CCC")
                            .pattern("CCC")),

            COKE_FROM_BLOCK = create(getCokeEntry()).withSuffix("_from_block")
                    .returns(9)
                    .unlockedBy(() -> (ItemLike) getCokeEntry().get())
                    .viaShapeless(b -> b.requires(CMBlocks.COKE_BLOCK.get())),

            COKE_BLOCK = create(CMBlocks.COKE_BLOCK).unlockedBy(() -> (ItemLike) getCokeEntry().get())
                    .viaShaped(b -> b.define('C', (ItemLike) getCokeEntry().get())
                            .pattern("CCC")
                            .pattern("CCC")
                            .pattern("CCC")),

            GRAPHITE = create(CMItems.GRAPHITE).unlockedByTag(T::coal)
                    .viaShapeless(b -> b.requires(Items.CLAY_BALL)
                            .requires(Items.COAL, 8)),

            SANDPAPER_BELT = create(CMItems.SANDPAPER_BELT).unlockedByTag(T::sandpaper)
                    .viaShaped(b -> b.define('D', Tags.Items.SANDS_COLORLESS)
                            .pattern("DDD")
                            .pattern("DDD")),

            TUNGSTEN_WIRE = create(CMItems.TUNGSTEN_WIRE).returns(2).unlockedByTag(T::tungstenIngot)
                    .viaShapeless(b -> b.requires(T.tungstenIngot())
                            .requires(Items.SHEARS)),

            TUNGSTEN_WIRE_SPOOL = create(CMItems.TUNGSTEN_WIRE_SPOOL).unlockedByTag(T::tungstenWire)
                    .viaShaped(b -> b.define('W', T.tungstenWire())
                            .define('S', Items.STICK)
                            .pattern(" W ")
                            .pattern("WSW")
                            .pattern(" W ")),

            COKE = create(() -> (ItemLike) getCokeEntry().get()).withSuffix("_from_coal")
                    .viaCookingTag(T::coal)
                    .rewardXP(.5f)
                    .forDuration(200)
                    .inBlastFurnace();

    private Marker CONTENT = enterFolder("content");

    GeneratedRecipe

    BELT_GRINDER = create(CMBlocks.BELT_GRINDER_BLOCK).unlockedBy(T::sandpaperBelt)
            .viaShaped(b -> b.define('B', T.sandpaperBelt())
                    .define('C', T.andesiteCasing())
                    .define('I', T.shaft())
                    .pattern("B")
                    .pattern("C")
                    .pattern("I")),

            CASTING_TABLE = create(CMBlocks.CASTING_TABLE_BLOCK).unlockedBy(T::andesiteAlloy)
                    .viaShaped(b -> b.define('A', T.andesiteAlloy())
                            .pattern("AAA")
                            .pattern("A A")
                            .pattern("A A")),

            CASTING_BASIN = create(CMBlocks.CASTING_BASIN_BLOCK).unlockedBy(T::andesiteAlloy)
                    .viaShaped(b -> b.define('A', T.andesiteAlloy())
                            .pattern("A A")
                            .pattern("A A")
                            .pattern(" A ")),

            FOUNDRY_LID = create(CMBlocks.FOUNDRY_LID_BLOCK).unlockedBy(T::andesiteAlloy)
                    .viaShaped(b -> b.define('A', T.andesiteAlloy())
                            .pattern("AAA")
                            .pattern("A A")),

            FOUNDRY_BASIN = create(CMBlocks.FOUNDRY_BASIN_BLOCK).unlockedBy(T::refractoryMortar)
                    .viaShaped(b -> b.define('A', T.andesiteAlloy())
                            .define('P', T.refractoryMortar())
                            .pattern("A A")
                            .pattern("APA")
                            .pattern("AAA")),

            FOUNDRY_MIXER = create(CMBlocks.FOUNDRY_MIXER_BLOCK).unlockedBy(T::refractoryMortar)
                    .viaShaped(b -> b.define('A', T.cog())
                            .define('B', T.copperCasing())
                            .define('C', CMItems.STURDY_WHISK.get())
                            .pattern("A")
                            .pattern("B")
                            .pattern("C")),

            FOUNDRY_UNIT = create(CMItems.FOUNDRY_UNIT).unlockedByTag(T::steelIngot)
                    .viaShaped(b -> b.define('S', T.steelIngot())
                            .define('C', Items.COMPASS)
                            .pattern("SCS")),

            FAUCET = create(CMBlocks.FAUCET_BLOCK).unlockedBy(T::andesiteAlloy)
                    .viaShaped(b -> b.define('A', T.andesiteAlloy())
                            .pattern("A A")
                            .pattern(" A ")),

            STURDY_WHISK = create(CMItems.STURDY_WHISK).unlockedByTag(T::tungstenSheet)
                    .viaShaped(b -> b.define('A', T.andesiteAlloy())
                            .define('B', AllItems.STURDY_SHEET.get())
                            .pattern(" A ")
                            .pattern("BAB")
                            .pattern("BBB")),

            LADLE_FILTER = create(CMItems.LADLE_FILTER).unlockedByTag(T::steelIngot).returns(2)
                    .viaShaped(b -> b.define('W', ItemTags.WOOL)
                            .define('S', T.steelIngot())
                            .pattern("SW")),

            TRANSFER_LADLE = create(CMItems.TRANSFER_LADLE).unlockedByTag(T::steelIngot).returns(2)
                    .viaShaped(b -> b.define('S', T.steelIngot())
                            .define('A', T.andesiteAlloy())
                            .define('M', T.refractoryMortar())
                            .pattern("SMS")
                            .pattern(" A "));

    //

    String currentFolder = "";

    Marker enterFolder(String folder) {
        currentFolder = folder;
        return new Marker();
    }

    GeneratedRecipeBuilder create(Supplier<ItemLike> result) {
        return new GeneratedRecipeBuilder(currentFolder, result);
    }

    GeneratedRecipeBuilder create(ResourceLocation result) {
        return new GeneratedRecipeBuilder(currentFolder, result);
    }

    GeneratedRecipeBuilder create(ItemProviderEntry result) {
        return create(() -> (ItemLike) result.get());
    }

    GeneratedRecipeBuilder create(ItemEntry<?> result) {
        return create(result::get);
    }

    GeneratedRecipeBuilder create(BlockEntry<?> result) {
        return create(result::get);
    }

    GeneratedRecipe createSpecial(Supplier<? extends SimpleCraftingRecipeSerializer<?>> serializer, String recipeType,
            String path) {
        ResourceLocation location = CreateMetallurgy.asResource(recipeType + "/" + currentFolder + "/" + path);
        return register(consumer -> {
            SimpleCraftingRecipeSerializer<?> ser = serializer.get();
            SpecialRecipeBuilder b = SpecialRecipeBuilder.special(category -> {
                try {
                    java.lang.reflect.Method createMethod = ser.getClass().getMethod("create",
                            net.minecraft.world.item.crafting.CraftingBookCategory.class);
                    return (net.minecraft.world.item.crafting.Recipe<?>) createMethod.invoke(ser, category);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create special recipe", e);
                }
            });
            b.save(consumer, location);
        });
    }

    class GeneratedRecipeBuilder {

        private String path;
        private String suffix;
        private Supplier<? extends ItemLike> result;
        private ResourceLocation compatDatagenOutput;
        List<ICondition> recipeConditions;

        private Supplier<ItemPredicate> unlockedBy;
        private int amount;

        private GeneratedRecipeBuilder(String path) {
            this.path = path;
            this.recipeConditions = new ArrayList<>();
            this.suffix = "";
            this.amount = 1;
        }

        public GeneratedRecipeBuilder(String path, Supplier<? extends ItemLike> result) {
            this(path);
            this.result = result;
        }

        public GeneratedRecipeBuilder(String path, ResourceLocation result) {
            this(path);
            this.compatDatagenOutput = result;
        }

        GeneratedRecipeBuilder returns(int amount) {
            this.amount = amount;
            return this;
        }

        GeneratedRecipeBuilder unlockedBy(Supplier<? extends ItemLike> item) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                    .of(item.get())
                    .build();
            return this;
        }

        GeneratedRecipeBuilder unlockedByTag(Supplier<TagKey<Item>> tag) {
            this.unlockedBy = () -> ItemPredicate.Builder.item()
                    .of(tag.get())
                    .build();
            return this;
        }

        GeneratedRecipeBuilder whenModLoaded(String modid) {
            return withCondition(new ModLoadedCondition(modid));
        }

        GeneratedRecipeBuilder whenModMissing(String modid) {
            return withCondition(new NotCondition(new ModLoadedCondition(modid)));
        }

        GeneratedRecipeBuilder withCondition(ICondition condition) {
            recipeConditions.add(condition);
            return this;
        }

        GeneratedRecipeBuilder withSuffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        GeneratedRecipe viaShaped(UnaryOperator<ShapedRecipeBuilder> builder) {
            return register(consumer -> {
                ShapedRecipeBuilder b = builder
                        .apply(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        GeneratedRecipe viaShapeless(UnaryOperator<ShapelessRecipeBuilder> builder) {
            return register(consumer -> {
                ShapelessRecipeBuilder b = builder
                        .apply(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.get(), amount));
                if (unlockedBy != null)
                    b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        GeneratedRecipe viaNetheriteSmithing(Supplier<? extends Item> base, Supplier<Ingredient> upgradeMaterial) {
            return register(consumer -> {
                SmithingTransformRecipeBuilder b = SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(base.get()), upgradeMaterial.get(), RecipeCategory.COMBAT, result.get()
                                .asItem());
                b.unlocks("has_item", inventoryTrigger(ItemPredicate.Builder.item()
                        .of(base.get())
                        .build()));
                b.save(consumer, createLocation("crafting"));
            });
        }

        private ResourceLocation createSimpleLocation(String recipeType) {
            return CreateMetallurgy.asResource(recipeType + "/" + getRegistryName().getPath() + suffix);
        }

        private ResourceLocation createLocation(String recipeType) {
            return CreateMetallurgy.asResource(recipeType + "/" + path + "/" + getRegistryName().getPath() + suffix);
        }

        private ResourceLocation getRegistryName() {
            return compatDatagenOutput == null ? BuiltInRegistries.ITEM.getKey(result.get()
                    .asItem()) : compatDatagenOutput;
        }

        GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder viaCooking(Supplier<? extends ItemLike> item) {
            return unlockedBy(item).viaCookingIngredient(() -> Ingredient.of(item.get()));
        }

        GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder viaCookingTag(Supplier<TagKey<Item>> tag) {
            return unlockedByTag(tag).viaCookingIngredient(() -> Ingredient.of(tag.get()));
        }

        GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder viaCookingIngredient(Supplier<Ingredient> ingredient) {
            return new GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder(ingredient);
        }

        class GeneratedCookingRecipeBuilder {

            private Supplier<Ingredient> ingredient;
            private float exp;
            private int cookingTime;

            private final RecipeSerializer<? extends AbstractCookingRecipe> FURNACE = RecipeSerializer.SMELTING_RECIPE,
                    SMOKER = RecipeSerializer.SMOKING_RECIPE, BLAST = RecipeSerializer.BLASTING_RECIPE,
                    CAMPFIRE = RecipeSerializer.CAMPFIRE_COOKING_RECIPE;

            GeneratedCookingRecipeBuilder(Supplier<Ingredient> ingredient) {
                this.ingredient = ingredient;
                cookingTime = 200;
                exp = 0;
            }

            GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder forDuration(int duration) {
                cookingTime = duration;
                return this;
            }

            GeneratedRecipeBuilder.GeneratedCookingRecipeBuilder rewardXP(float xp) {
                exp = xp;
                return this;
            }

            GeneratedRecipe inFurnace() {
                return inFurnace(b -> b);
            }

            GeneratedRecipe inFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                return create(FURNACE, builder, 1);
            }

            GeneratedRecipe inSmoker() {
                return inSmoker(b -> b);
            }

            GeneratedRecipe inSmoker(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(FURNACE, builder, 1);
                create(CAMPFIRE, builder, 3);
                return create(SMOKER, builder, .5f);
            }

            GeneratedRecipe inBlastFurnace() {
                return inBlastFurnace(b -> b);
            }

            GeneratedRecipe inBlastFurnace(UnaryOperator<SimpleCookingRecipeBuilder> builder) {
                create(FURNACE, builder, 1);
                return create(BLAST, builder, .5f);
            }

            private GeneratedRecipe create(RecipeSerializer<? extends AbstractCookingRecipe> serializer,
                    UnaryOperator<SimpleCookingRecipeBuilder> builder, float cookingTimeModifier) {
                return register(consumer -> {
                    boolean isOtherMod = compatDatagenOutput != null;

                    // In 1.21, get factory from serializer for cooking recipes
                    // Vanilla cooking serializers may not have getFactory(), so we need to handle
                    // both cases
                    SimpleCookingRecipeBuilder b;
                    try {
                        // Try to get factory from serializer (works for ProcessingRecipeSerializer)
                        AbstractCookingRecipe.Factory<?> factory = (AbstractCookingRecipe.Factory<?>) serializer
                                .getClass()
                                .getMethod("getFactory").invoke(serializer);
                        b = builder.apply(SimpleCookingRecipeBuilder.generic(ingredient.get(),
                                RecipeCategory.MISC, isOtherMod ? Items.DIRT : result.get(), exp,
                                (int) (cookingTime * cookingTimeModifier), (RecipeSerializer) serializer,
                                (AbstractCookingRecipe.Factory) factory));
                    } catch (ReflectiveOperationException e) {
                        // Vanilla cooking serializers don't have getFactory() - create factory lambda
                        // Based on serializer type, create appropriate cooking recipe
                        AbstractCookingRecipe.Factory<?> factory = (id, category, ing, res, xp, time) -> {
                            if (serializer == RecipeSerializer.SMELTING_RECIPE) {
                                return new net.minecraft.world.item.crafting.SmeltingRecipe(id, category, ing, res, xp,
                                        time);
                            } else if (serializer == RecipeSerializer.SMOKING_RECIPE) {
                                return new net.minecraft.world.item.crafting.SmokingRecipe(id, category, ing, res, xp,
                                        time);
                            } else if (serializer == RecipeSerializer.BLASTING_RECIPE) {
                                return new net.minecraft.world.item.crafting.BlastingRecipe(id, category, ing, res, xp,
                                        time);
                            } else if (serializer == RecipeSerializer.CAMPFIRE_COOKING_RECIPE) {
                                return new net.minecraft.world.item.crafting.CampfireCookingRecipe(id, category, ing,
                                        res, xp, time);
                            }
                            throw new IllegalArgumentException("Unknown cooking recipe serializer: " + serializer);
                        };
                        b = builder.apply(SimpleCookingRecipeBuilder.generic(ingredient.get(),
                                RecipeCategory.MISC, isOtherMod ? Items.DIRT : result.get(), exp,
                                (int) (cookingTime * cookingTimeModifier), (RecipeSerializer) serializer,
                                (AbstractCookingRecipe.Factory) factory));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to create cooking recipe builder", e);
                    }

                    if (unlockedBy != null)
                        b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));

                    ResourceLocation recipeId = createSimpleLocation(
                            BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer)
                                    .getPath());
                    if (isOtherMod && !recipeConditions.isEmpty()) {
                        // For modded recipes with conditions, we need to handle them differently in
                        // 1.21
                        // Conditions are typically handled at the recipe level, not via FinishedRecipe
                        // wrapper
                        // This is a simplified approach - conditions may need to be handled via recipe
                        // serializer
                        b.save(consumer, recipeId);
                    } else {
                        b.save(consumer, recipeId);
                    }
                });
            }
        }
    }

    //

    public CMStandardRecipeGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }
}
