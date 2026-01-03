package fr.lucreeper74.createmetallurgy.content.items.ladle_filter;

import com.simibubi.create.content.logistics.filter.FilterItemStack;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class LadleFilterItemStack extends FilterItemStack {

    public String AddressFilter;
    private final CompoundTag fluidFilterTag;
    private FluidStack cachedFluidFilter;
    int filledAmount;
    int comparator;

    public LadleFilterItemStack(ItemStack filter) {
        super(filter);
        var customData = filter.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        boolean defaults = customData == null;
        CompoundTag tag = defaults ? new CompoundTag() : customData.copyTag();

        filledAmount = defaults ? -1 : tag.getInt("FilledAmount");
        AddressFilter = defaults ? "*" : tag.getString("Address");
        comparator = defaults ? 0 : tag.getInt("Comparator");

        // Store the fluid filter tag for lazy parsing when we have registry access
        CompoundTag fluidTag = tag.getCompound("FluidFilter");
        fluidFilterTag = fluidTag.isEmpty() ? null : fluidTag;
        cachedFluidFilter = null; // Will be parsed lazily in test() method
    }

    /**
     * Gets the fluid filter, parsing it from NBT if needed using the provided
     * registry access.
     * This is called lazily in the test() method when we have access to
     * Level.registryAccess().
     */
    private FluidStack getFluidFilter(net.minecraft.core.HolderLookup.Provider registries) {
        if (cachedFluidFilter == null) {
            if (fluidFilterTag == null || fluidFilterTag.isEmpty()) {
                cachedFluidFilter = FluidStack.EMPTY;
            } else {
                // Parse with registry access
                cachedFluidFilter = FluidStack.parseOptional(registries, fluidFilterTag);
                if (cachedFluidFilter == null) {
                    cachedFluidFilter = FluidStack.EMPTY;
                }
            }
        }
        return cachedFluidFilter;
    }

    @Override
    public boolean test(Level world, ItemStack stack, boolean matchNBT) {
        if (super.test(world, stack, matchNBT))
            return true;

        if (LadleItem.isLadle(stack)) {
            boolean address_match = LadleItem.matchAddress(stack, AddressFilter) || AddressFilter.contentEquals("*");
            var ladleTank = LadleItem.getFluidContents(stack, world.registryAccess());
            FluidStack ladleFluid = ladleTank.getFluid();
            // Parse fluid filter with registry access from the world
            FluidStack filterFluid = getFluidFilter(world.registryAccess());
            boolean fluid_match = filterFluid.isEmpty()
                    || FluidStack.isSameFluidSameComponents(filterFluid, ladleFluid);

            boolean filled_match = true;

            if (filledAmount >= 0) {
                int fluidAmount = LadleItem.getFluidAmount(stack);
                filled_match = switch (comparator) {
                    case 1 -> fluidAmount > filledAmount;
                    case 2 -> fluidAmount >= filledAmount;
                    case 3 -> fluidAmount < filledAmount;
                    case 4 -> fluidAmount <= filledAmount;
                    default -> fluidAmount == filledAmount;
                };
            }

            return address_match && filled_match && fluid_match;
        }
        return false;
    }

    @Override
    public boolean test(Level world, FluidStack stack, boolean matchNBT) {
        return false;
    }
}