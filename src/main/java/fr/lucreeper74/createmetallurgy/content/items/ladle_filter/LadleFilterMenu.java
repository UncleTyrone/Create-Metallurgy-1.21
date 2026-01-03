package fr.lucreeper74.createmetallurgy.content.items.ladle_filter;

import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import com.tterrag.registrate.util.nullness.NonnullType;
import fr.lucreeper74.createmetallurgy.registries.CMMenuTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class LadleFilterMenu extends AbstractFilterMenu {

    String address;
    FluidStack fluidFilter;
    int filledAmount;
    int comparator;

    /**
     * Gets registry access from the player's inventory.
     * Returns null if player is not available (shouldn't happen in normal usage).
     */
    private HolderLookup.Provider getRegistryAccess() {
        // Get player from inventory - PlayerInventory extends Inventory and has player
        // access
        if (this.player instanceof Player player) {
            return player.registryAccess();
        }
        // Fallback: try to get from inventory if it's a PlayerInventory
        // This is a safety fallback, but in practice player should always be available
        return null;
    }

    public LadleFilterMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        this(type, id, inv, ItemStack.STREAM_CODEC.decode(extraData));
    }

    public LadleFilterMenu(@NonnullType MenuType<?> type, int id, Inventory inv, ItemStack stack) {
        super(type, id, inv, stack);
    }

    public static LadleFilterMenu create(int id, Inventory inv, ItemStack stack) {
        return new LadleFilterMenu(CMMenuTypes.LADLE_FILTER.get(), id, inv, stack);
    }

    @Override
    protected int getPlayerInventoryXOffset() {
        return 40;
    }

    @Override
    protected int getPlayerInventoryYOffset() {
        return 101;
    }

    @Override
    protected void addFilterSlots() {
        this.addSlot(new SlotItemHandler(ghostInventory, 0, 16, 56));
    }

    @Override
    protected void init(Inventory inv, ItemStack contentHolderIn) {
        super.init(inv, contentHolderIn);
        ghostInventory.setStackInSlot(0, fluidFilter.getFluid().getBucket().getDefaultInstance());
    }

    @Override
    protected ItemStackHandler createGhostInventory() {
        return new ItemStackHandler();
    }

    @Override
    public void clearContents() {
        address = "*";
        fluidFilter = FluidStack.EMPTY;
        filledAmount = -1;
        comparator = 0;
        ghostInventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    @Override
    protected void initAndReadInventory(ItemStack filterItem) {
        super.initAndReadInventory(filterItem);
        var customData = filterItem.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        boolean defaults = customData == null;
        CompoundTag tag = defaults ? new CompoundTag() : customData.copyTag();

        filledAmount = defaults ? -1 : tag.getInt("FilledAmount");
        address = defaults ? "*" : tag.getString("Address");
        comparator = defaults ? 0 : tag.getInt("Comparator");

        // Parse fluid filter with registry access from player
        CompoundTag fluidTag = tag.getCompound("FluidFilter");
        HolderLookup.Provider registries = getRegistryAccess();
        if (fluidTag.isEmpty()) {
            fluidFilter = FluidStack.EMPTY;
        } else if (registries != null) {
            fluidFilter = FluidStack.parseOptional(registries, fluidTag);
            if (fluidFilter == null) {
                fluidFilter = FluidStack.EMPTY;
            }
        } else {
            // Fallback if registry access is not available (shouldn't happen in normal
            // usage)
            fluidFilter = FluidStack.EMPTY;
        }
    }

    @Override
    protected void saveData(ItemStack filterItem) {
        super.saveData(filterItem);
        CompoundTag tag = filterItem.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        tag.putString("Address", address);

        // Save fluid filter with registry access from player
        HolderLookup.Provider registries = getRegistryAccess();
        net.minecraft.nbt.Tag fluidTag;
        if (registries != null) {
            fluidTag = fluidFilter.saveOptional(registries);
        } else {
            // Fallback if registry access is not available (shouldn't happen in normal
            // usage)
            fluidTag = new CompoundTag();
        }
        if (fluidTag instanceof CompoundTag compoundTag) {
            tag.put("FluidFilter", compoundTag);
        } else {
            tag.put("FluidFilter", new CompoundTag());
        }
        tag.putInt("FilledAmount", filledAmount);
        tag.putInt("Comparator", comparator);
        filterItem.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.of(tag));
    }
}