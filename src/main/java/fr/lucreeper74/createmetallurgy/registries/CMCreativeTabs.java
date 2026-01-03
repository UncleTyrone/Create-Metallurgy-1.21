package fr.lucreeper74.createmetallurgy.registries;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.*;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import fr.lucreeper74.createmetallurgy.content.blocks.light_bulb.LightBulbBlock;
import fr.lucreeper74.createmetallurgy.content.fluids.TagDependentBucketItem;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB;

public class CMCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(CREATIVE_MODE_TAB,
            CreateMetallurgy.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_CREATIVE_TAB = REGISTER.register(
            "main_group",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + CreateMetallurgy.MOD_ID + ".main_group"))
                    .icon(CMItems.OBDURIUM_INGOT::asStack)
                    .displayItems(createDisplayItemsGenerator())
                    .build());

    private static RegistrateDisplayItemsGenerator createDisplayItemsGenerator() {
        // Use a supplier pattern to avoid forward reference issues
        // The tab will be available when the generator actually runs
        return new RegistrateDisplayItemsGenerator(() -> CMCreativeTabs.MAIN_CREATIVE_TAB);
    }

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }

    public static class RegistrateDisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {

        private final java.util.function.Supplier<DeferredHolder<CreativeModeTab, CreativeModeTab>> tabFilterSupplier;

        public RegistrateDisplayItemsGenerator(
                java.util.function.Supplier<DeferredHolder<CreativeModeTab, CreativeModeTab>> tabFilterSupplier) {
            this.tabFilterSupplier = tabFilterSupplier;
        }

        private DeferredHolder<CreativeModeTab, CreativeModeTab> getTabFilter() {
            return tabFilterSupplier.get();
        }

        private List<Item> collectBlocks(Predicate<Item> exclusionPredicate) {
            List<Item> items = new ReferenceArrayList<>();
            for (RegistryEntry<Block, Block> entry : CreateMetallurgy.REGISTRATE.getAll(Registries.BLOCK)) {
                // Only add items that are assigned to our creative tab (or null, meaning
                // default)
                // This prevents duplicates when Registrate's event listener also tries to add
                // items
                if (!CreateRegistrate.isInCreativeTab(entry, getTabFilter()))
                    continue;
                Item item = entry.get()
                        .asItem();
                if (item == Items.AIR)
                    continue;
                if (!exclusionPredicate.test(item))
                    items.add(item);
            }
            items = new ReferenceArrayList<>(new ReferenceLinkedOpenHashSet<>(items));
            return items;
        }

        private List<Item> collectItems(Predicate<Item> exclusionPredicate) {
            List<Item> items = new ReferenceArrayList<>();

            for (RegistryEntry<Item, Item> entry : CreateMetallurgy.REGISTRATE.getAll(Registries.ITEM)) {
                // Only add items that are assigned to our creative tab (or null, meaning
                // default)
                // This prevents duplicates when Registrate's event listener also tries to add
                // items
                if (!CreateRegistrate.isInCreativeTab(entry, getTabFilter()))
                    continue;
                Item item = entry.get();
                if (item instanceof BlockItem)
                    continue;
                if (!exclusionPredicate.test(item))
                    items.add(item);
            }
            return items;
        }

        private static void outputAll(CreativeModeTab.Output output, List<Item> items,
                Function<Item, CreativeModeTab.TabVisibility> visibilityFunc) {
            for (Item item : items) {
                output.accept(item, visibilityFunc.apply(item));
            }
        }

        private static Function<Item, CreativeModeTab.TabVisibility> makeVisibilityFunc() {
            Map<Item, CreativeModeTab.TabVisibility> visibilities = new Reference2ObjectOpenHashMap<>();

            for (BlockEntry<LightBulbBlock> entry : CMBlocks.LIGHT_BULBS) {
                LightBulbBlock block = entry.get();
                if (block.getColor() != DyeColor.WHITE) {
                    visibilities.put(entry.asItem(), CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
                }
            }

            return item -> {
                CreativeModeTab.TabVisibility visibility = visibilities.get(item);
                if (visibility != null) {
                    return visibility;
                }
                return CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
            };
        }

        private static Predicate<Item> makeExclusionPredicate() {
            Set<Item> exclusions = new ReferenceOpenHashSet<>();

            List<ItemProviderEntry<?, ?>> simpleExclusions = List.of(
                    CMItems.INCOMPLETE_INDUSTRIAL_CRUCIBLE);

            List<TagDependentBucketItem> tagDependentExclusions = CMFluids.ALL_MODDED_FLUIDS.stream()
                    .map(entry -> (TagDependentBucketItem) entry.getBucket().get())
                    .toList();

            for (ItemProviderEntry<?, ?> entry : simpleExclusions) {
                exclusions.add(entry.asItem());
            }

            for (TagDependentBucketItem bucketItem : tagDependentExclusions) {
                if (bucketItem.shouldHide()) {
                    exclusions.add(bucketItem);
                }
            }

            return exclusions::contains;
        }

        @Override
        public void accept(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
            Function<Item, CreativeModeTab.TabVisibility> visibilityFunc = makeVisibilityFunc();
            Predicate<Item> exclusionPredicate = makeExclusionPredicate();

            List<Item> items = new LinkedList<>();
            items.addAll(collectBlocks(exclusionPredicate));
            items.addAll(collectItems(exclusionPredicate));

            outputAll(output, items, visibilityFunc);
        }
    }
}