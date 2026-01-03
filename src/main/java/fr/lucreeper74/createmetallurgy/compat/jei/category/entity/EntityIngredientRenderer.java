package fr.lucreeper74.createmetallurgy.compat.jei.category.entity;


import com.mojang.blaze3d.vertex.PoseStack;
import fr.lucreeper74.createmetallurgy.content.blocks.industrial_crucible.foundry.recipes.EntityIngredient;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventory;

public record EntityIngredientRenderer(int scale) implements IIngredientRenderer<EntityIngredient.EntityStack> {

    @Override
    public void render(GuiGraphics graphics, @NotNull EntityIngredient.EntityStack entityInput) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level != null) {
            Entity entity = entityInput.type().create(level);

            if (entity instanceof LivingEntity livingEntity) { // No recipes with Non-living entity anyway
                float entityScale = scale;
                float maxSize = entity.getBbHeight() + entity.getBbWidth();
                entityScale /= maxSize;

                Quaternionf rotation1 = (new Quaternionf()).rotationY(((float) Math.PI/180f) * 160f);
                Quaternionf rotation2 = (new Quaternionf()).rotationZ((float) Math.PI);
                livingEntity.setYHeadRot(0);
                // Minecraft 1.21 renderEntityInInventory signature: (GuiGraphics, float, float, float, Vector3f, Quaternionf, Quaternionf, LivingEntity)
                org.joml.Vector3f translation = new org.joml.Vector3f(0, 0, 0);
                renderEntityInInventory(graphics, -15f, 25f, entityScale, translation, rotation1, rotation2, livingEntity);
            }
        }
        matrixStack.popPose();
    }

    @Override
    public List<Component> getTooltip(EntityIngredient.EntityStack entityInput, TooltipFlag tooltipFlag) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(entityInput.type().getDescription());
        if (tooltipFlag.isAdvanced())
            tooltip.add((Component.literal(EntityType.getKey(entityInput.type()).toString())).withStyle(ChatFormatting.DARK_GRAY));

        return tooltip;
    }
}