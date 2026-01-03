package fr.lucreeper74.createmetallurgy.content.blocks.casting.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;

public abstract class CastingOutput {
    public static final CastingOutput EMPTY = CastingOutput.fromStack(ItemStack.EMPTY);

    // CODEC for serializing/deserializing CastingOutput using existing JSON
    // serialization
    public static final Codec<CastingOutput> CODEC = Codec.PASSTHROUGH.comapFlatMap(
            dynamic -> {
                try {
                    JsonElement je = (JsonElement) dynamic.getValue();
                    return DataResult.success(deserialize(je));
                } catch (Exception e) {
                    return DataResult.error(() -> "Failed to parse CastingOutput: " + e.getMessage());
                }
            },
            output -> {
                JsonElement je = output.serialize();
                return new com.mojang.serialization.Dynamic<>(JsonOps.INSTANCE, je);
            });

    // StreamCodec for network serialization
    public static final StreamCodec<RegistryFriendlyByteBuf, CastingOutput> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            output -> {
                // For network, we serialize as ItemStack (simplified)
                ItemStack stack = output.getStack();
                return BuiltInRegistries.ITEM.getKey(stack.getItem());
            },
            net.minecraft.network.codec.ByteBufCodecs.INT,
            output -> output.getStack().getCount(),
            (itemId, count) -> {
                Item item = BuiltInRegistries.ITEM.get(itemId);
                return fromStack(new ItemStack(item, count));
            });

    public abstract ItemStack getStack();

    public abstract JsonElement serialize();

    public static CastingOutput fromStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return EMPTY;
        }
        return new StackOutput(stack);
    }

    public static CastingOutput fromTag(TagKey<Item> tag, int count) {
        return new TagOutput(tag, count);
    }

    public static CastingOutput deserialize(JsonElement je) {
        if (!je.isJsonObject())
            throw new JsonSyntaxException("CastingOutput must be a json object");

        JsonObject json = je.getAsJsonObject();
        int count = GsonHelper.getAsInt(json, "count", 1);
        // Handle "id" as an alias for "item" (for 1.21 format compatibility)
        if (json.has("item") || json.has("id")) {
            String itemId = json.has("item") ? GsonHelper.getAsString(json, "item") : GsonHelper.getAsString(json, "id");
            ItemStack itemstack = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId)), count);
            return CastingOutput.fromStack(itemstack);
        } else if (json.has("tag")) {
            String rawTag = GsonHelper.getAsString(json, "tag");
            TagKey<Item> tag = TagKey.create(Registries.ITEM, ResourceLocation.parse(rawTag));
            return CastingOutput.fromTag(tag, count);
        } else
            throw new JsonParseException("An CastingOutput entry needs either a tag or an item");
    }

    public void write(FriendlyByteBuf buf) {
        // Serialize ItemStack to NBT - registry access not available in
        // RecipeSerializer context
        // Use basic serialization that works without full registry access
        ItemStack stack = getStack();
        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        nbt.putInt("Count", stack.getCount());
        // Note: DataComponents not serialized here - basic ItemStack only
        buf.writeNbt(nbt);
    }

    public static CastingOutput read(FriendlyByteBuf buf) {
        CompoundTag nbt = buf.readNbt();
        if (nbt == null)
            return CastingOutput.EMPTY;
        // Parse ItemStack from NBT - registry access not available in RecipeSerializer
        // context
        ResourceLocation itemId = ResourceLocation.parse(nbt.getString("id"));
        Item item = BuiltInRegistries.ITEM.get(itemId);
        int count = nbt.getInt("Count");
        ItemStack stack = new ItemStack(item, count);
        // Note: DataComponents not restored here - basic ItemStack only
        return CastingOutput.fromStack(stack);
    }

    /**
     * Class for CastingOutput from an ItemStack
     */
    private static class StackOutput extends CastingOutput {
        private final ItemStack stack;

        private StackOutput(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public ItemStack getStack() {
            return stack;
        }

        @Override
        public JsonElement serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
            int count = stack.getCount();
            if (count > 1)
                json.addProperty("count", count);
            return json;
        }
    }

    /**
     * Class for CastingOutput from a Tag
     */
    private static class TagOutput extends CastingOutput {
        private final TagKey<Item> tag;
        private final int count;

        private TagOutput(TagKey<Item> tag, int count) {
            this.tag = tag;
            this.count = count;
        }

        @Override
        public ItemStack getStack() {
            var tagResult = BuiltInRegistries.ITEM.getTag(tag);
            if (tagResult.isPresent() && tagResult.get().size() > 0) {
                Iterator<Item> items = tagResult.get().stream().map(h -> h.value()).iterator();
                if (items.hasNext())
                    return new ItemStack(items.next(), count);
            }
            ItemStack barrier = new ItemStack(net.minecraft.world.level.block.Blocks.BARRIER);
            barrier.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                    net.minecraft.network.chat.Component.literal("Empty Tag: " + this.tag.location()));
            return barrier;
        }

        @Override
        public JsonElement serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("tag", tag.location().toString());
            if (count > 1)
                json.addProperty("count", count);
            return json;
        }
    }
}