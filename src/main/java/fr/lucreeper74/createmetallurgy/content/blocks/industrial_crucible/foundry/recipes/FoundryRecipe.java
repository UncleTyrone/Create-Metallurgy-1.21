package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.MeltingInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FoundryRecipe extends ProcessingRecipe<RecipeInput, ProcessingRecipeParams> {

    protected int minHeat = -50;
    protected int maxHeat = 50;

    public FoundryRecipe(IRecipeTypeInfo typeInfo, ProcessingRecipeParams params) {
        super(typeInfo, params);
        validate(typeInfo.getId().toString());
    }

    public FoundryRecipe withHeatRange(int minHeat, int maxHeat) {
        this.minHeat = minHeat;
        this.maxHeat = maxHeat;
        return this;
    }

    private void validate(String recipeTypeId) {
        String messageHeader = "Your custom recipe (" + recipeTypeId + ")";
        Logger logger = CreateMetallurgy.LOGGER;

        if (minHeat > maxHeat) {
            logger.warn(messageHeader + " specified a minimum heat value greater than the maximum value.");
        }
    }

    protected boolean canSpecifyDuration() {
        return true;
    }

    public static boolean bulkMatch(CrucibleBlockEntity be, Recipe<?> recipe) {
        if (recipe instanceof ProcessingRecipe<?, ?> processRecipe) {
            boolean matchItem = true;

            List<Ingredient> ingredients = processRecipe.getIngredients();
            if (!ingredients.isEmpty()) {
                List<Integer> toExclude = new ArrayList<>();

                Ingredients: for (Ingredient item : ingredients) {
                    MeltingInventory inv = be.foundry.getInventory();

                    for (int i = 0; i < inv.getSlots(); i++) {
                        if (toExclude.contains(i))
                            continue;

                        if (item.test(inv.getSlot(i).getStack())) {
                            toExclude.add(i);
                            continue Ingredients;
                        }
                    }

                    // No matching item
                    matchItem = false;
                }
            }
            return fluidMatch(be, recipe) && matchItem;
        }
        return false;
    }

    public static boolean fluidMatch(CrucibleBlockEntity be, Recipe<?> recipe) {
        if (recipe instanceof ProcessingRecipe<?, ?> processRecipe) {
            List<SizedFluidIngredient> fluidIngredients = processRecipe.getFluidIngredients();
            if (!fluidIngredients.isEmpty()) {
                FluidIngredient: for (SizedFluidIngredient fluidIngredient : fluidIngredients) {

                    for (FluidStack fluid : be.getTank().fluids) {
                        if (fluidIngredient.test(fluid) && fluidIngredient.amount() <= fluid.getAmount())
                            continue FluidIngredient;
                    }
                    // No matching fluid
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean matchSpecific(ItemStack stack, Recipe<?> recipe) {
        if (recipe instanceof ProcessingRecipe<?, ?> processRecipe) {
            return processRecipe.getIngredients().get(0).test(stack);
        }
        return false;
    }

    public static boolean isEnoughHeated(CrucibleBlockEntity ladle, ProcessingRecipe<?, ?> recipe) {
        if (recipe == null)
            return false;

        int currentHeat = ladle.foundry.getCurrentHeat();

        if (recipe instanceof FoundryRecipe foundryRecipe)
            return currentHeat >= foundryRecipe.getMinHeat() && currentHeat <= foundryRecipe.getMaxHeat();
        else
            return currentHeat >= getHeatRequirement(recipe);
    }

    public static int getHeatRequirement(ProcessingRecipe<?, ?> recipe) {
        if (recipe == null)
            return 0;

        return switch (recipe.getRequiredHeat()) {
            case NONE -> 0;
            case HEATED -> 2;
            case SUPERHEATED -> 3;
        };
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 1;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 10;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 10;
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false;
    }

    public int getMaxHeat() {
        return maxHeat;
    }

    public int getMinHeat() {
        return minHeat;
    }
}
