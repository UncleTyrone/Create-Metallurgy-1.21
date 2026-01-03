package fr.lucreeper74.createmetallurgy.data.recipe.createmetallurgy;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.EntityMeltingRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.EntityMeltingRecipeBuilder;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.FoundryRecipe;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.FoundryRecipeBuilder;
import fr.lucreeper74.createmetallurgy.data.recipe.CMMetals;
import fr.lucreeper74.createmetallurgy.data.recipe.CMRecipeProvider;
import fr.lucreeper74.createmetallurgy.registries.CMFluids;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

import java.util.concurrent.CompletableFuture;

import java.util.function.UnaryOperator;

import static fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.FoundryRecipeBuilder.DEFAULT_MAX_HEAT;

@SuppressWarnings("unused")
public class FoundryRecipeGen extends CMRecipeProvider {

    GeneratedRecipe

    /* Bulk Melting Recipes */
    ALL_METALS = allMetals(),

            /* Entity Melting Recipes */
            IRON_GOLEM = meltingEntity("iron_golem", EntityType.IRON_GOLEM, 6, CMFluids.MOLTEN_IRON, 135, 9),
            ZOMBIFIED_PIGLIN = meltingEntity("zombified_piglin", EntityType.ZOMBIFIED_PIGLIN, 4, CMFluids.MOLTEN_GOLD,
                    40, 6),
            PIGLIN = meltingEntity("piglin", EntityType.PIGLIN, 4, CMFluids.MOLTEN_GOLD, 10, 6),
            PIGLIN_BRUTE = meltingEntity("piglin_brute", EntityType.PIGLIN_BRUTE, 4, CMFluids.MOLTEN_GOLD, 20, 6),

            WITHER_SKELETON = createEntity("wither_skeleton", b -> (EntityMeltingRecipeBuilder) b
                    .requireEntity(EntityType.WITHER_SKELETON, 4)
                    .requiresMinHeat(9)
                    .require(CMFluids.MOLTEN_IRON.get(), 270)
                    .output(CMFluids.MOLTEN_STEEL.get(), 270));

    protected GeneratedRecipe allMetals() {
        for (CMMetals metal : CMMetals.values()) {
            CMMetals.ItemType block = CMMetals.ItemType.BLOCK;
            // Block
            meltingTag(metal.getName() + "/block", metal.getItemTag(block), metal.getFluid(), block.getFluidAmount(),
                    getMetalHeat(metal), (int) (CMRecipeProvider.MELTING_DURATION * block.getDurationFactor() * .7f));
        }
        return null;
    }

    //

    /**
     * Recipe heat condition for metal based on metal fluid temp
     */
    protected int getMetalHeat(CMMetals metal) {
        return (int) (((float) DEFAULT_MAX_HEAT / (HEAT_CONDITION_THRESHOLD * 5)) * metal.getMeltingPoint());
    }

    /**
     * Recipes with input Tag :
     *
     * @param recipeId Recipe name / folders
     * @param inputTag Item tag input
     * @param result   Fluid result
     * @param amount   Fluid amount
     * @param minHeat  Minimum Heat condition
     * @param duration Processing time
     */
    protected GeneratedRecipe meltingTag(String recipeId, TagKey<Item> inputTag,
            FluidEntry<BaseFlowingFluid.Flowing> result, int amount, int minHeat, int duration) {
        RecipeSerializer<?> serializer = CMRecipeTypes.BULK_MELTING.getSerializer();
        return create(recipeId, serializer, (FoundryRecipeBuilder<FoundryRecipe> b) -> {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            FoundryRecipeBuilder<FoundryRecipe> builder = (FoundryRecipeBuilder<FoundryRecipe>) (ProcessingRecipeBuilder) b
                    .requiresMinHeat(minHeat)
                    .withCondition(new NotCondition(new TagEmptyCondition(inputTag.location())))
                    .require(inputTag)
                    .duration(duration)
                    .output(result.get(), amount);
            return builder;
        });
    }

    /**
     * Recipes with input Items :
     *
     * @param recipeId Recipe name / folders
     * @param input    Item input
     * @param result   Fluid result
     * @param amount   Fluid amount
     * @param minHeat  Minimum Heat condition
     * @param duration Processing time
     */
    protected GeneratedRecipe meltingItem(String recipeId, ItemLike input, FluidEntry<BaseFlowingFluid.Flowing> result,
            int amount, int minHeat, int duration) {
        RecipeSerializer<?> serializer = CMRecipeTypes.BULK_MELTING.getSerializer();
        return create(recipeId, serializer, (FoundryRecipeBuilder<FoundryRecipe> b) -> {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            FoundryRecipeBuilder<FoundryRecipe> builder = (FoundryRecipeBuilder<FoundryRecipe>) (ProcessingRecipeBuilder) b
                    .requiresMinHeat(minHeat)
                    .require(input)
                    .duration(duration)
                    .output(result.get(), amount);
            return builder;
        });
    }

    /**
     * Reci
     *
     * 
     * @param recipeId   Recipe name / folders$
     * @param entityType EntityType input
     * @param damage     Damage to the entity input
     * @param result     Fluid result
     * @param amount     Fluid amount
     * @param minHeat    Minimum Heat condition
     */
    protected GeneratedRecipe meltingEntity(String recipeId, EntityType<?> entityType, int damage,
            FluidEntry<BaseFlowingFluid.Flowing> result, int amount, int minHeat) {
        return createEntity(recipeId, b -> (EntityMeltingRecipeBuilder) b
                .requireEntity(entityType, damage)
                .requiresMinHeat(minHeat)
                .output(result.get(), amount));
    }

    //

    public FoundryRecipeGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registries);
    }

    protected GeneratedRecipe create(String name, RecipeSerializer<?> serializer,
            UnaryOperator<FoundryRecipeBuilder<FoundryRecipe>> transform) {
        GeneratedRecipe generatedRecipe = c -> {
            // Get factory from serializer using reflection
            ProcessingRecipe.Factory<?, ?> factory;
            try {
                factory = (ProcessingRecipe.Factory<?, ?>) serializer.getClass().getMethod("getFactory")
                        .invoke(serializer);
            } catch (Exception e) {
                throw new RuntimeException("Failed to get factory from serializer", e);
            }
            FoundryRecipeBuilder<FoundryRecipe> builder = transform
                    .apply(new FoundryRecipeBuilder<>(factory, CreateMetallurgy.asResource(name)));
            builder.build(c);
        };
        all.add(generatedRecipe);
        return generatedRecipe;
    }

    protected GeneratedRecipe createEntity(String name, UnaryOperator<EntityMeltingRecipeBuilder> transform) {
        RecipeSerializer<?> serializer = CMRecipeTypes.ENTITY_MELTING.getSerializer();
        GeneratedRecipe generatedRecipe = c -> {
            // Get factory from serializer using reflection
            ProcessingRecipe.Factory<?, ?> factory;
            try {
                factory = (ProcessingRecipe.Factory<?, ?>) serializer.getClass().getMethod("getFactory")
                        .invoke(serializer);
            } catch (Exception e) {
                throw new RuntimeException("Failed to get factory from serializer", e);
            }
            transform.apply(new EntityMeltingRecipeBuilder(factory, CreateMetallurgy.asResource(name)))
                    .build(c);
        };
        all.add(generatedRecipe);
        return generatedRecipe;
    }
}