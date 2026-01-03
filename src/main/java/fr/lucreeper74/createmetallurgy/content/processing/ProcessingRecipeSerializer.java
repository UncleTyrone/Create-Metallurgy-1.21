package fr.lucreeper74.createmetallurgy.content.processing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeInput;

public class ProcessingRecipeSerializer<T extends ProcessingRecipe<RecipeInput, ProcessingRecipeParams>>
        implements RecipeSerializer<T> {

    private final ProcessingRecipe.Factory<ProcessingRecipeParams, T> factory;

    @SuppressWarnings("unchecked")
    public ProcessingRecipeSerializer(Object factory) {
        // The factory is passed as a method reference like MeltingRecipe::new
        // which is actually ProcessingRecipe.Factory<ProcessingRecipeParams, T>
        this.factory = (ProcessingRecipe.Factory<ProcessingRecipeParams, T>) factory;
    }

    public ProcessingRecipe.Factory<ProcessingRecipeParams, T> getFactory() {
        return factory;
    }

    @Override
    public com.mojang.serialization.MapCodec<T> codec() {
        // Get Create's base recipe codec
        MapCodec<T> baseCodec = ProcessingRecipe.codec(factory, ProcessingRecipeParams.CODEC);
        Codec<T> baseCodecCodec = baseCodec.codec();

        // Create a codec that pre-processes JSON to separate fluid ingredients
        Codec<T> preprocessedCodec = Codec.PASSTHROUGH.comapFlatMap(
                dynamic -> {
                    try {
                        JsonElement je = (JsonElement) dynamic.convert(JsonOps.INSTANCE).getValue();
                        if (!je.isJsonObject()) {
                            return DataResult.error(() -> "Recipe must be a JSON object");
                        }

                        JsonObject json = je.getAsJsonObject().deepCopy();

                        // Pre-process ingredients array to separate item and fluid ingredients
                        if (json.has("ingredients") && json.get("ingredients").isJsonArray()) {
                            JsonArray ingredientsArray = json.getAsJsonArray("ingredients");
                            JsonArray itemIngredients = new JsonArray();
                            JsonArray fluidIngredients = new JsonArray();

                            for (JsonElement element : ingredientsArray) {
                                if (element.isJsonObject()) {
                                    JsonObject obj = element.getAsJsonObject();
                                    if (obj.has("fluid")) {
                                        int amount = GsonHelper.getAsInt(obj, "amount", 0);
                                        if (amount > 0) {
                                            fluidIngredients.add(element);
                                        }
                                    } else {
                                        itemIngredients.add(element);
                                    }
                                } else {
                                    itemIngredients.add(element);
                                }
                            }

                            json.remove("ingredients");
                            // Always add ingredients field, even if empty (Create's codec expects it)
                            json.add("ingredients", itemIngredients);
                            if (!fluidIngredients.isEmpty()) {
                                json.add("fluidIngredients", fluidIngredients);
                            }
                        }

                        return baseCodecCodec.parse(JsonOps.INSTANCE, json);
                    } catch (Exception e) {
                        return DataResult.error(() -> "Failed to pre-process recipe: " + e.getMessage());
                    }
                },
                recipe -> {
                    JsonElement encoded = baseCodecCodec.encodeStart(JsonOps.INSTANCE, recipe)
                            .result()
                            .orElseThrow();

                    if (encoded.isJsonObject()) {
                        JsonObject json = encoded.getAsJsonObject();

                        // Merge fluidIngredients back into ingredients if present
                        if (json.has("fluidIngredients") && json.has("ingredients")) {
                            JsonArray ingredients = json.getAsJsonArray("ingredients");
                            JsonArray fluidIngredients = json.getAsJsonArray("fluidIngredients");
                            for (JsonElement fluidIng : fluidIngredients) {
                                ingredients.add(fluidIng);
                            }
                            json.remove("fluidIngredients");
                        } else if (json.has("fluidIngredients") && !json.has("ingredients")) {
                            JsonArray fluidIngredients = json.getAsJsonArray("fluidIngredients");
                            json.remove("fluidIngredients");
                            json.add("ingredients", fluidIngredients);
                        }

                        return new Dynamic<>(JsonOps.INSTANCE, json);
                    }

                    return new Dynamic<>(JsonOps.INSTANCE, encoded);
                });

        // Use the preprocessed codec by wrapping the base MapCodec
        // We need to implement all abstract methods, delegating to the base codec
        // but using our preprocessed codec for the actual parsing
        return new MapCodec<T>() {
            @Override
            public <O> com.mojang.serialization.DataResult<T> decode(com.mojang.serialization.DynamicOps<O> ops,
                    com.mojang.serialization.MapLike<O> input) {
                // Convert MapLike to JSON, then use preprocessed codec which handles the
                // pre-processing
                try {
                    // Convert MapLike to JSON
                    java.util.Map<String, JsonElement> jsonMap = new java.util.HashMap<>();
                    input.entries().forEach(pair -> {
                        O key = pair.getFirst();
                        O value = pair.getSecond();

                        // Convert key and value to JSON
                        ops.getStringValue(key).result().ifPresent(keyStr -> {
                            JsonElement valueJson = (JsonElement) ops.convertTo(JsonOps.INSTANCE, value);
                            jsonMap.put(keyStr, valueJson);
                        });
                    });

                    // Build JsonObject from map
                    JsonObject json = new JsonObject();
                    jsonMap.forEach(json::add);

                    // Pre-process ingredients array to separate item and fluid ingredients
                    if (json.has("ingredients") && json.get("ingredients").isJsonArray()) {
                        JsonArray ingredientsArray = json.getAsJsonArray("ingredients");
                        JsonArray itemIngredients = new JsonArray();
                        JsonArray fluidIngredients = new JsonArray();

                        for (JsonElement element : ingredientsArray) {
                            if (element.isJsonObject()) {
                                JsonObject obj = element.getAsJsonObject();
                                if (obj.has("fluid")) {
                                    int amount = GsonHelper.getAsInt(obj, "amount", 0);
                                    if (amount > 0) {
                                        fluidIngredients.add(element);
                                    }
                                } else {
                                    itemIngredients.add(element);
                                }
                            } else {
                                itemIngredients.add(element);
                            }
                        }

                        json.remove("ingredients");
                        // Always add ingredients field, even if empty (Create's codec expects it)
                        json.add("ingredients", itemIngredients);
                        if (!fluidIngredients.isEmpty()) {
                            json.add("fluidIngredients", fluidIngredients);
                        }
                    } else if (!json.has("ingredients")) {
                        // Ensure ingredients field exists even if missing (Create's codec expects it)
                        json.add("ingredients", new JsonArray());
                    }

                    // Convert pre-processed JSON back to the original ops format and use base codec
                    O processed = JsonOps.INSTANCE.convertTo(ops, json);
                    com.mojang.serialization.MapLike<O> processedMapLike = ops.getMap(processed)
                            .result()
                            .orElseThrow(() -> new RuntimeException("Failed to convert JSON back to MapLike"));
                    return baseCodec.decode(ops, processedMapLike);
                } catch (Exception e) {
                    return DataResult.error(() -> "Failed to decode recipe: " + e.getMessage());
                }
            }

            @Override
            public <O> com.mojang.serialization.RecordBuilder<O> encode(T input,
                    com.mojang.serialization.DynamicOps<O> ops, com.mojang.serialization.RecordBuilder<O> builder) {
                // Use base codec for encoding (it handles the structure correctly)
                return baseCodec.encode(input, ops, builder);
            }

            @Override
            public Codec<T> codec() {
                return preprocessedCodec;
            }

            @Override
            public <O> java.util.stream.Stream<O> keys(com.mojang.serialization.DynamicOps<O> ops) {
                // Delegate to base codec for keys
                return baseCodec.keys(ops);
            }
        };
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        // Create's ProcessingRecipe uses a stream codec that requires the factory and
        // params stream codec
        return ProcessingRecipe.streamCodec(factory, ProcessingRecipeParams.STREAM_CODEC);
    }
}
