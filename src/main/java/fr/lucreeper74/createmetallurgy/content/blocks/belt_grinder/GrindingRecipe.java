package fr.lucreeper74.createmetallurgy.content.blocks.belt_grinder;

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import fr.lucreeper74.createmetallurgy.compat.jei.category.CMSequencedAssemblySubCategory;
import fr.lucreeper74.createmetallurgy.registries.CMBlocks;
import fr.lucreeper74.createmetallurgy.utils.CMLang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class GrindingRecipe extends ProcessingRecipe<RecipeInput, ProcessingRecipeParams> implements IAssemblyRecipe {

	public GrindingRecipe(IRecipeTypeInfo type, ProcessingRecipeParams params) {
        super(type, params);
    }

    @Override
    public boolean matches(RecipeInput input, Level worldIn) {
        if (input.isEmpty())
            return false;
        return ingredients.get(0)
                .test(input.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 4;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Component getDescriptionForAssembly() {
        return CMLang.translateDirect("recipe.assembly.grinding");
    }

    @Override
    public void addRequiredMachines(Set<ItemLike> list) {
        list.add(CMBlocks.BELT_GRINDER_BLOCK.get());
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {}

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> CMSequencedAssemblySubCategory.AssemblyGrinding::new;
    }
}