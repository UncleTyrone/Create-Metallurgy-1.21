package fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.CrucibleBlockEntity;
import fr.lucreeper74.createmetallurgy.registries.CMRecipeTypes;
import net.minecraft.world.entity.EntityType;

public class EntityMeltingRecipe extends FoundryRecipe {

    protected EntityIngredient entityIngredient;

    public EntityMeltingRecipe(ProcessingRecipeParams params) {
        super(CMRecipeTypes.ENTITY_MELTING, params);
        this.entityIngredient = EntityIngredient.EMPTY;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return false;
    }

    public boolean matches(CrucibleBlockEntity be, EntityType<?> type) {
        return bulkMatch(be, this) && entityIngredient.test(type);
    }

    public EntityIngredient getEntityIngredient() {
        return entityIngredient;
    }

    public EntityMeltingRecipe withEntityIngredient(EntityIngredient ingredient) {
        this.entityIngredient = ingredient;
        return this;
    }
}
