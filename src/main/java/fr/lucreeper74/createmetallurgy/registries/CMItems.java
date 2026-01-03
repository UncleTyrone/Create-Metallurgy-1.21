package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import fr.lucreeper74.createmetallurgy.content.items.CokeItem;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.ItemEntry;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleStyles;
import fr.lucreeper74.createmetallurgy.content.items.FoundryUnitItem;
import fr.lucreeper74.createmetallurgy.content.items.ladle_filter.LadleFilterItem;
import fr.lucreeper74.createmetallurgy.data.recipe.CMMetals;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;

import static com.simibubi.create.AllTags.AllItemTags.*;
import static fr.lucreeper74.createmetallurgy.CreateMetallurgy.REGISTRATE;
import static fr.lucreeper74.createmetallurgy.registries.CMTags.CMItemTags.*;
import static net.neoforged.neoforge.common.Tags.Items.*;

@SuppressWarnings("unused")
public class CMItems {

        // Note: setCreativeTab is not used because we have a custom DisplayItemsGenerator
        // that handles all item registration. Using both causes duplicate entries.

        public static final ItemEntry<Item> OBDURIUM_INGOT = taggedIngredientFireResistant("obdurium_ingot",
                        CMMetals.OBDURIUM.getItemTag(CMMetals.ItemType.INGOT), Tags.Items.INGOTS),
                        OBDURIUM_SHEET = taggedIngredientFireResistant("obdurium_sheet",
                                        CMMetals.OBDURIUM.getItemTag(CMMetals.ItemType.PLATE), PLATES.tag);

        public static final ItemEntry<Item> RAW_WOLFRAMITE = taggedIngredient("raw_tungsten",
                        CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.RAW_MATERIAL), RAW_MATERIALS),
                        CRUSHED_RAW_WOLFRAMITE = taggedIngredient("crushed_raw_tungsten", CRUSHED_RAW_MATERIALS.tag);

        public static final ItemEntry<Item> TUNGSTEN_INGOT = taggedIngredientFireResistant("tungsten_ingot",
                        CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.INGOT), Tags.Items.INGOTS),
                        TUNGSTEN_SHEET = taggedIngredientFireResistant("tungsten_sheet",
                                        CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.PLATE), PLATES.tag),
                        TUNGSTEN_NUGGET = taggedIngredientFireResistant("tungsten_nugget",
                                        CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.NUGGET), NUGGETS),
                        TUNGSTEN_WIRE = taggedIngredientFireResistant("tungsten_wire",
                                        CMMetals.TUNGSTEN.getItemTag(CMMetals.ItemType.WIRE), WIRES.tag);

        public static final ItemEntry<Item> GRAPHITE_BLANK_MOLD = taggedIngredient("graphite_blank_mold",
                        GRAPHITE_MOLDS.tag),
                        GRAPHITE_INGOT_MOLD = taggedIngredient("graphite_ingot_mold", GRAPHITE_MOLDS.tag),
                        GRAPHITE_NUGGET_MOLD = taggedIngredient("graphite_nugget_mold", GRAPHITE_MOLDS.tag),
                        GRAPHITE_PLATE_MOLD = taggedIngredient("graphite_plate_mold", GRAPHITE_MOLDS.tag),
                        GRAPHITE_ROD_MOLD = taggedIngredient("graphite_rod_mold", GRAPHITE_MOLDS.tag),
                        GRAPHITE_GEAR_MOLD = taggedIngredient("graphite_gear_mold", GRAPHITE_MOLDS.tag);

        // Metal Dust
        public static final ItemEntry<TagDependentIngredientItem> WOLFRAMITE_DUST = compatDust(CMMetals.TUNGSTEN,
                        CMMetals.ItemType.DUST),
                        GOLD_DUST = compatDust(CMMetals.GOLD, CMMetals.ItemType.DUST),
                        IRON_DUST = compatDust(CMMetals.IRON, CMMetals.ItemType.DUST),
                        COPPER_DUST = compatDust(CMMetals.COPPER, CMMetals.ItemType.DUST),
                        ZINC_DUST = compatDust(CMMetals.ZINC, CMMetals.ItemType.DUST);

        // Metal Dirty Dust
        public static final ItemEntry<TagDependentIngredientItem> DIRTY_WOLFRAMITE_DUST = compatDust(CMMetals.TUNGSTEN,
                        CMMetals.ItemType.DIRTY_DUST),
                        DIRTY_GOLD_DUST = compatDust(CMMetals.GOLD, CMMetals.ItemType.DIRTY_DUST),
                        DIRTY_IRON_DUST = compatDust(CMMetals.IRON, CMMetals.ItemType.DIRTY_DUST),
                        DIRTY_COPPER_DUST = compatDust(CMMetals.COPPER, CMMetals.ItemType.DIRTY_DUST),
                        DIRTY_ZINC_DUST = compatDust(CMMetals.ZINC, CMMetals.ItemType.DIRTY_DUST);

        public static final ItemEntry<Item> SLAG = taggedIngredient("slag", CMTags.CMItemTags.SLAG.tag);

        public static final ItemEntry<CokeItem> COKE = REGISTRATE.item("coke", CokeItem::new)
                        .tag(COAL_COKE.tag)
                        .register();

        public static final ItemEntry<FoundryUnitItem> FOUNDRY_UNIT = REGISTRATE
                        .item("foundry_unit", FoundryUnitItem::new)
                        .register();

        public static final ItemEntry<Item> GRAPHITE = taggedIngredient("graphite", CMTags.CMItemTags.GRAPHITE.tag),
                        STEEL_INGOT = taggedIngredient("steel_ingot",
                                        CMMetals.STEEL.getItemTag(CMMetals.ItemType.INGOT), Tags.Items.INGOTS),
                        STURDY_WHISK = REGISTRATE.item("sturdy_whisk", Item::new).register(),
                        TUNGSTEN_WIRE_SPOOL = REGISTRATE.item("tungsten_wire_spool", Item::new).register(),
                        SANDPAPER_BELT = REGISTRATE.item("sandpaper_belt", Item::new).register();

        public static final ItemEntry<LadleFilterItem> LADLE_FILTER = REGISTRATE
                        .item("ladle_filter", LadleFilterItem::new)
                        .register();

        public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_INDUSTRIAL_CRUCIBLE = sequencedIngredient(
                        "incomplete_industrial_crucible", AllTags.AllItemTags.UPRIGHT_ON_BELT.tag);

        public static final ItemEntry<LadleItem> TRANSFER_LADLE = REGISTRATE
                        .item("transfer_ladle", p -> new LadleItem(p, LadleStyles.getDefaultStyle()))
                        .properties(p -> p.stacksTo(1))
                        .tag(PACKAGES.tag, LADLE.tag, NOT_UPRIGHT_ON_BELT.tag)
                        .model((c, p) -> p.withExistingParent(c.getName(),
                                        p.modLoc("item/ladle/" + LadleStyles.getDefaultStyle().type())))
                        .register();

        // Shortcuts
        private static ItemEntry<TagDependentIngredientItem> compatDust(CMMetals metal, CMMetals.ItemType dustType) {
                if (!dustType.equals(CMMetals.ItemType.DIRTY_DUST) && !dustType.equals(CMMetals.ItemType.DUST))
                        return null;

                ItemBuilder<TagDependentIngredientItem, CreateRegistrate> itemEntry = REGISTRATE
                                .item((dustType.equals(CMMetals.ItemType.DIRTY_DUST) ? "dirty_" : "") + metal.getName()
                                                + "_dust",
                                                props -> new TagDependentIngredientItem(props,
                                                                metal.getItemTag(dustType)))
                                .tag(metal.getItemTag(dustType));

                if (dustType == CMMetals.ItemType.DIRTY_DUST) {
                        itemEntry.tag(DIRTY_DUSTS.tag);
                } else if (dustType == CMMetals.ItemType.DUST) {
                        itemEntry.tag(DUSTS);
                }

                return itemEntry.register();
        }

        @SafeVarargs
        private static ItemEntry<Item> taggedIngredient(String name, TagKey<Item>... tags) {
                return REGISTRATE.item(name, Item::new)
                                .tag(tags)
                                .register();
        }

        @SafeVarargs
        private static ItemEntry<Item> taggedIngredientFireResistant(String name, TagKey<Item>... tags) {
                return REGISTRATE.item(name, Item::new)
                                .tag(tags)
                                .properties(Item.Properties::fireResistant)
                                .register();
        }

        private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
                return REGISTRATE.item(name, SequencedAssemblyItem::new)
                                .register();
        }

        @SafeVarargs
        private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name, TagKey<Item>... tags) {
                return REGISTRATE.item(name, SequencedAssemblyItem::new)
                                .tag(tags)
                                .register();
        }

        public static void register() {
        }
}