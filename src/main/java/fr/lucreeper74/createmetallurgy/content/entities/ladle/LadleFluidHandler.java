package fr.lucreeper74.createmetallurgy.content.entities.ladle;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LadleFluidHandler implements IFluidHandlerItem {
    public static final int LADLE_CAPACITY = 9000; // in mb

    @NotNull
    protected ItemStack container;

    @Nullable
    protected HolderLookup.Provider registries;

    public LadleFluidHandler(@NotNull ItemStack container, @Nullable HolderLookup.Provider registries) {
        this.container = container;
        this.registries = registries;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return container;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @NotNull
    public FluidStack getFluid() {
        return LadleItem.getFluidContents(container, registries).getFluid();
    }

    protected void setFluid(FluidStack fluid) {
        FluidTank tank = new FluidTank(LADLE_CAPACITY);
        tank.setFluid(fluid);

        LadleItem.setFluidContents(container, tank, registries);
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return LADLE_CAPACITY;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;
    }

    public boolean isEmpty() {
        return getFluid().isEmpty();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(0, resource))
            return 0;

        FluidStack fluid = getFluid();
        if (action.simulate()) {
            if (fluid.isEmpty())
                return Math.min(getTankCapacity(0), resource.getAmount());
            if (!FluidStack.isSameFluidSameComponents(fluid, resource))
                return 0;
            return Math.min(getTankCapacity(0) - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty()) {
            fluid = resource.copyWithAmount(Math.min(getTankCapacity(0), resource.getAmount()));
            onContentsChanged(fluid);
            return fluid.getAmount();
        }
        if (!FluidStack.isSameFluidSameComponents(fluid, resource))
            return 0;
        int filled = getTankCapacity(0) - fluid.getAmount();

        if (resource.getAmount() < filled) {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else
            fluid.setAmount(getTankCapacity(0));
        if (filled > 0)
            onContentsChanged(fluid);
        return filled;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack fluid = getFluid();
        if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, fluid)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        int drained = maxDrain;
        FluidStack fluid = getFluid();
        if (fluid.getAmount() < drained) {
            drained = fluid.getAmount();
        }
        FluidStack stack = fluid.copyWithAmount(drained);
        if (action.execute() && drained > 0) {
            fluid.shrink(drained);
            onContentsChanged(fluid);
        }
        return stack;
    }

    protected void onContentsChanged(FluidStack newFluid) {
        LadleItem.setNextAddrs(container);
        setFluid(newFluid);
    }
}
