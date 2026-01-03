package fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public abstract class CastingRecipeSerializer implements RecipeSerializer<CastingRecipe> {

    // Record to hold parsed ingredients from the array
    private record ParsedIngredients(Ingredient itemIngredient, SizedFluidIngredient fluidIngredient) {
    }

    protected void writeToJson(JsonObject json, CastingRecipe recipe) {
        // Add id field for 1.21 format

        json.addProperty("id", recipe.getId().toString());

        JsonArray jsonIngredients = new JsonArray();

        Ingredient ingredient = recipe.ingredient;
        if (!ingredient.isEmpty())
            jsonIngredients.add(Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, ingredient).result().orElseThrow());

        SizedFluidIngredient fluidIngredient = recipe.fluidIngredient;
        if (fluidIngredient != null && fluidIngredient.amount() > 0) {
            // Use FLAT_CODEC for serialization
            jsonIngredients.add(SizedFluidIngredient.FLAT_CODEC.encodeStart(JsonOps.INSTANCE, fluidIngredient).result()
                    .orElseThrow());
        }

        json.add("ingredients", jsonIngredients);
        json.add("result", recipe.result.serialize());

        int processingDuration = recipe.getProcessingDuration();
        if (processingDuration > 0)
            json.addProperty("processingTime", processingDuration);

        if (recipe.moldConsumed)
            json.addProperty("mold_consumed", true);
    }

    // Note: fromJson is no longer part of RecipeSerializer interface in 1.21
    // This method is kept for compatibility with data generation
    public CastingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        CastingRecipe recipe = createRecipe(recipeId);

        for (JsonElement je : GsonHelper.getAsJsonArray(json, "ingredients")) {
            if (je.isJsonObject() && je.getAsJsonObject().has("fluid")) {
                // Parse SizedFluidIngredient using FLAT_CODEC
                recipe.fluidIngredient = SizedFluidIngredient.FLAT_CODEC.parse(JsonOps.INSTANCE, je)
                        .result()
                        .orElse(null); // Will use default from constructor if null
            } else
                recipe.ingredient = Ingredient.CODEC.parse(JsonOps.INSTANCE, je).result().orElse(Ingredient.EMPTY);
        }

        recipe.processingDuration = GsonHelper.getAsInt(json, "processingTime");
        recipe.moldConsumed = GsonHelper.getAsBoolean(json, "mold_consumed", false);

        JsonElement je = GsonHelper.getAsJsonObject(json, "result");
        if (je.isJsonObject() && !GsonHelper.isValidNode(je.getAsJsonObject(), "fluid"))
            recipe.result = CastingOutput.deserialize(je);

        return recipe;
    }

    // Note: In 1.21, RecipeSerializer requires codec() and streamCodec() methods
    // The old fromJson/toNetwork methods are no longer part of the interface
    @Override
    public MapCodec<CastingRecipe> codec() {
        // Create a codec that directly handles JsonArray without intermediate parsing
        // This prevents the codec system from trying to parse array elements as
        // Ingredient first
        Codec<JsonArray> jsonArrayCodec = Codec.PASSTHROUGH.comapFlatMap(
                dynamic -> {
                    try {
                        JsonElement je = (JsonElement) dynamic.convert(JsonOps.INSTANCE).getValue();
                        if (je.isJsonArray()) {
                            return com.mojang.serialization.DataResult.success(je.getAsJsonArray());
                        }
                        return com.mojang.serialization.DataResult.error(() -> "Expected JSON array");
                    } catch (Exception e) {
                        return com.mojang.serialization.DataResult
                                .error(() -> "Failed to parse as JSON array: " + e.getMessage());
                    }
                },
                jsonArray -> new Dynamic<>(JsonOps.INSTANCE, jsonArray));

        // Custom codec for ParsedIngredients that works directly with JsonArray
        Codec<ParsedIngredients> parsedIngredientsCodec = jsonArrayCodec.comapFlatMap(
                jsonArray -> {
                    try {
                        final Ingredient[] itemIngredientRef = { Ingredient.EMPTY };
                        final SizedFluidIngredient[] fluidIngredientRef = { null };

                        for (JsonElement element : jsonArray) {
                            if (element.isJsonObject()) {
                                JsonObject json = element.getAsJsonObject();
                                if (json.has("fluid")) {
                                    // Check amount before parsing to avoid "Size must be positive" error
                                    int amount = GsonHelper.getAsInt(json, "amount", 0);
                                    if (amount > 0) {
                                        // Parse fluid ingredient only if amount is positive
                                        SizedFluidIngredient.FLAT_CODEC.parse(JsonOps.INSTANCE, element)
                                                .result()
                                                .ifPresent(fi -> fluidIngredientRef[0] = fi);
                                    }
                                } else {
                                    // Parse item ingredient
                                    Ingredient.CODEC.parse(JsonOps.INSTANCE, element)
                                            .result()
                                            .ifPresent(ing -> {
                                                if (!ing.isEmpty()) {
                                                    itemIngredientRef[0] = ing;
                                                }
                                            });
                                }
                            }
                        }

                        return com.mojang.serialization.DataResult.success(
                                new ParsedIngredients(itemIngredientRef[0], fluidIngredientRef[0]));
                    } catch (Exception e) {
                        return com.mojang.serialization.DataResult
                                .error(() -> "Failed to parse ingredients: " + e.getMessage());
                    }
                },
                parsed -> {
                    JsonArray jsonArray = new JsonArray();
                    if (!parsed.itemIngredient().isEmpty()) {
                        jsonArray.add(Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, parsed.itemIngredient())
                                .result().orElseThrow());
                    }
                    SizedFluidIngredient fi = parsed.fluidIngredient();
                    if (fi != null && fi.amount() > 0) {
                        jsonArray.add(SizedFluidIngredient.FLAT_CODEC.encodeStart(JsonOps.INSTANCE, fi)
                                .result().orElseThrow());
                    }
                    return jsonArray; // This will be converted to Dynamic by jsonArrayCodec
                });

        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(CastingRecipe::getId),
                // Parse ingredients array: extract item and fluid ingredients separately
                parsedIngredientsCodec.optionalFieldOf("ingredients", new ParsedIngredients(Ingredient.EMPTY, null))
                        .forGetter(
                                recipe -> new ParsedIngredients(recipe.getIngredient(), recipe.getFluidIngredient())),
                CastingOutput.CODEC.fieldOf("result").forGetter(CastingRecipe::getResult),
                Codec.INT.optionalFieldOf("processingTime", 0).forGetter(CastingRecipe::getProcessingDuration),
                Codec.BOOL.optionalFieldOf("mold_consumed", false).forGetter(CastingRecipe::isMoldConsumed))
                .apply(instance, (id, parsedIngredients, result, processingTime, moldConsumed) -> {
                    CastingRecipe recipe = createRecipe(id);
                    recipe.ingredient = parsedIngredients.itemIngredient();
                    // Only set fluidIngredient if it's valid (amount > 0), otherwise use recipe's
                    // default
                    SizedFluidIngredient fi = parsedIngredients.fluidIngredient();
                    if (fi != null && fi.amount() > 0) {
                        recipe.fluidIngredient = fi;
                    }
                    // Otherwise, recipe.fluidIngredient already has the default from constructor
                    recipe.result = result;
                    recipe.processingDuration = processingTime;
                    recipe.moldConsumed = moldConsumed;
                    return recipe;
                }));

    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CastingRecipe> streamCodec() {
        return StreamCodec.composite(
                ResourceLocation.STREAM_CODEC,
                recipe -> recipe.getId(),
                Ingredient.CONTENTS_STREAM_CODEC,
                recipe -> recipe.getIngredient(),
                SizedFluidIngredient.STREAM_CODEC,
                recipe -> recipe.getFluidIngredient(),
                CastingOutput.STREAM_CODEC,
                recipe -> recipe.getResult(),
                net.minecraft.network.codec.ByteBufCodecs.INT,
                recipe -> recipe.getProcessingDuration(),
                net.minecraft.network.codec.ByteBufCodecs.BOOL,
                recipe -> recipe.isMoldConsumed(),
                (id, ingredient, fluidIngredient, result, processingTime, moldConsumed) -> {
                    CastingRecipe recipe = createRecipe(id);
                    recipe.ingredient = ingredient;
                    recipe.fluidIngredient = fluidIngredient;
                    recipe.result = result;
                    recipe.processingDuration = processingTime;
                    recipe.moldConsumed = moldConsumed;
                    return recipe;
                });
    }

    public final void write(JsonObject json, CastingRecipe recipe) {
        writeToJson(json, recipe);
    }

    public abstract CastingRecipe createRecipe(ResourceLocation id);

    public static class CastingTableRecipeSerializer extends CastingRecipeSerializer {
        @Override
        public CastingRecipe createRecipe(ResourceLocation id) {
            return new CastingTableRecipe(id);
        }
    }

    public static class CastingBasinRecipeSerializer extends CastingRecipeSerializer {
        @Override
        public CastingRecipe createRecipe(ResourceLocation id) {
            return new CastingBasinRecipe(id);
        }
    }
}