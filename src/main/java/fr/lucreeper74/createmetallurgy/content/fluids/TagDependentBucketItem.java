package fr.lucreeper74.createmetallurgy.content.fluids;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public class TagDependentBucketItem extends BucketItem {

    private TagKey<Item> tag;

    public TagDependentBucketItem(Holder<Fluid> content, Item.Properties builder, TagKey<Item> tag) {
        super(content.value(), builder);
        this.tag = tag;
    }

    public boolean shouldHide() {
        var tagResult = BuiltInRegistries.ITEM.getTag(tag);
        return tagResult.isEmpty() || tagResult.get().size() == 0;
    }
}
