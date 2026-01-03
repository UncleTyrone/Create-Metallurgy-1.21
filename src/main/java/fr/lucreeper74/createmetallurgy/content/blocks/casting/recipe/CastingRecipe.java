package fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.core.HolderLookup;
import fr.lucreeper74.createmetallurgy.content.blocks.casting.CastingBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.slf4j.Logger;

public abstract class CastingRecipe implements Recipe<RecipeInput> {

    protected final ResourceLocation id;
    protected SizedFluidIngredient fluidIngredient;
    protected Ingredient ingredient;
    protected int processingDuration;
    protected boolean moldConsumed;
    protected CastingOutput result;

    public CastingRecipe(ResourceLocation id) {
        this.id = id;
        this.ingredient = Ingredient.EMPTY;
        this.fluidIngredient = createEmptyFluidIngredient();
        this.processingDuration = 0;
        this.moldConsumed = false;
        this.result = CastingOutput.EMPTY;

        validate(id);
    }

    private static SizedFluidIngredient createEmptyFluidIngredient() {
        // Return null - we'll handle null in getFluidIngredient() to return a safe default
        // We can't use SizedFluidIngredient.of(..., 0) because it throws "Size must be positive"
        return null;
    }

    private void validate(ResourceLocation recipeTypeId) {
        String messageHeader = "Your custom recipe (" + recipeTypeId + ")";
        Logger logger = CreateMetallurgy.LOGGER;

        if (ingredient.isEmpty() && moldConsumed) {
            logger.warn(messageHeader
                    + " specified a mold condition. Mold conditions have no impact on this recipe cause there is no mold.");
        }
    }

    public static boolean match(CastingBlockEntity be, Recipe<?> recipe) {
        if (recipe instanceof CastingRecipe castingRecipe) {
            FluidStack fluidInBuffer = be.getFluidBuffer();
            ItemStack mold = be.moldInv.getStackInSlot(0);
            Ingredient ingredient = castingRecipe.getIngredient();

            // Handle null or invalid fluidIngredient - if it's null or has amount <= 0, it should match any fluid
            SizedFluidIngredient fluidIng = castingRecipe.fluidIngredient;
            boolean fluidMatches = (fluidIng == null || fluidIng.amount() <= 0) || fluidIng.test(fluidInBuffer);
            boolean hasMold = !ingredient.isEmpty();
            boolean ingredientMatches = hasMold && ingredient.test(mold);

            return fluidMatches && (!hasMold || ingredientMatches);
        }
        return false;
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.getStack();
    }

    public ResourceLocation getId() {
        return id;
    }

    @Override
    public abstract RecipeSerializer<?> getSerializer();

    @Override
    public RecipeType<?> getType() {
        return getTypeInfo().getType();
    }

    public abstract IRecipeTypeInfo getTypeInfo();

    public Ingredient getIngredient() {
        return ingredient;
    }

    public SizedFluidIngredient getFluidIngredient() {
        // Return a safe default if fluidIngredient is null or has invalid amount
        // Use a valid fluid with amount 1 as default - this won't match anything in practice
        // because recipes without fluid ingredients shouldn't be processed anyway
        if (fluidIngredient == null || fluidIngredient.amount() <= 0) {
            return SizedFluidIngredient.of(net.minecraft.world.level.material.Fluids.WATER, 1);
        }
        return fluidIngredient;
    }
    
    public boolean hasFluidIngredient() {
        return fluidIngredient != null && fluidIngredient.amount() > 0;
    }

    public int getProcessingDuration() {
        return processingDuration;
    }

    public boolean isMoldConsumed() {
        return moldConsumed;
    }

    public CastingOutput getResult() {
        return result;
    }
}