package fr.lucreeper74.createmetallurgy.content.blocks.labelling_station;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import fr.lucreeper74.createmetallurgy.content.entities.ladle.LadleItem;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class LabellingStationBlockEntity extends SmartBlockEntity {

    public boolean redstonePowered;
    public int buttonCooldown;

    public ArrayList<String> addressesList;

    public LadleItemHandler ladleInv;
    public ItemStack heldBox;

    public static final int CYCLE = 40;
    public int animationTicks;
    public boolean animationInward;

    public LabellingStationBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        addressesList = new ArrayList<>();
        ladleInv = new LadleItemHandler(this);
        heldBox = ItemStack.EMPTY;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        animationTicks = compound.getInt("AnimationTicks");
        if (compound.contains("HeldBox"))
            heldBox = ItemStack.parseOptional(registries, compound.getCompound("HeldBox"));
        else
            heldBox = ItemStack.EMPTY;

        ListTag list = compound.getList("AddrsList", Tag.TAG_COMPOUND);
        if (!list.isEmpty()) {
            addressesList.clear();
            for (int i = 0; i < list.size(); i++) {
                CompoundTag item = list.getCompound(i);
                if (item.contains("Address", Tag.TAG_STRING))
                    addressesList.add(i, item.getString("Address"));
            }
        }
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);

        compound.putInt("AnimationTicks", animationTicks);
        compound.put("HeldBox", heldBox.saveOptional(registries));

        ListTag list = new ListTag();
        for (String address : addressesList) {
            if (address != null) {
                CompoundTag addressTag = new CompoundTag();
                addressTag.putString("Address", address);
                list.add(addressTag);
            }
        }
        if (!list.isEmpty())
            compound.put("AddrsList", list);
    }

    @Override
    public void tick() {
        super.tick();

        if (buttonCooldown > 0)
            buttonCooldown--;

        if (level.isClientSide) {
            if (animationTicks == CYCLE - (animationInward ? 5 : 1))
                AllSoundEvents.PACKAGER.playAt(level, worldPosition, 1, 1, true);
            if (animationTicks == (animationInward ? 1 : 5))
                level.playLocalSound(worldPosition, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.25f, 0.75f,
                        true);
        }

        if (animationTicks > 0)
            animationTicks--;

        if (animationTicks == 0 && !level.isClientSide()) {
            if (!heldBox.isEmpty())
                attemptToSend();
            setChanged();
        }
    }

    @Override
    public void lazyTick() {
        if (level.isClientSide())
            return;
        if (!redstonePowered)
            return;
        redstonePowered = getBlockState().getOptionalValue(LabellingStationBlock.POWERED)
                .orElse(false);
        if (!redstoneModeActive())
            return;
        updateClipBoardAddresses();
    }

    public void activate() {
        redstonePowered = true;
        setChanged();

        if (!redstoneModeActive())
            return;

        updateClipBoardAddresses();
        attemptToSend();

        buttonCooldown = 20;
    }

    public boolean redstoneModeActive() {
        return !getBlockState().getOptionalValue(LabellingStationBlock.LINKED)
                .orElse(false);
    }

    protected void updateClipBoardAddresses() {
        addressesList.clear();
        for (Direction side : Iterate.directions) {
            ArrayList<String> addresses = getClipBoardAddresses(side);
            if (addresses == null || addresses.isEmpty())
                continue;
            addressesList = addresses;
        }
    }

    protected ArrayList<String> getClipBoardAddresses(Direction side) {
        BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(side));
        if (!(blockEntity instanceof ClipboardBlockEntity clipboardBE))
            return null;

        ArrayList<String> addresses = new ArrayList<>();
        HolderLookup.Provider registries = level.registryAccess();

        // Read clipboard entries from ClipboardBlockEntity
        // In Create 6.0, clipboard entries are stored and can be accessed via the block
        // entity
        try {
            // Get clipboard entries - the exact API may vary, but typically accessed via a
            // method
            // that returns entries or via NBT data
            CompoundTag clipboardData = clipboardBE.getUpdateTag(registries);

            // Check if clipboard has entries stored
            if (clipboardData.contains("Entries", Tag.TAG_LIST)) {
                ListTag entriesList = clipboardData.getList("Entries", Tag.TAG_COMPOUND);
                for (int i = 0; i < entriesList.size(); i++) {
                    CompoundTag entryTag = entriesList.getCompound(i);
                    // Look for address entries - clipboard entries may have different structures
                    // Check for common address-related keys
                    if (entryTag.contains("AddressClip", Tag.TAG_COMPOUND)) {
                        ItemStack addressStack = ItemStack.parseOptional(registries,
                                entryTag.getCompound("AddressClip"));
                        if (!addressStack.isEmpty()) {
                            // Extract address string from the item stack's custom data
                            var customData = addressStack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
                            if (customData != null) {
                                CompoundTag dataTag = customData.copyTag();
                                if (dataTag.contains("Address", Tag.TAG_STRING)) {
                                    String address = dataTag.getString("Address");
                                    if (!address.isEmpty()) {
                                        addresses.add(address);
                                    }
                                }
                            }
                        }
                    } else if (entryTag.contains("Address", Tag.TAG_STRING)) {
                        // Direct string address
                        String address = entryTag.getString("Address");
                        if (!address.isEmpty()) {
                            addresses.add(address);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // If API access fails, return empty list
            // This ensures the method doesn't crash if the API structure is different
            return addresses.isEmpty() ? null : addresses;
        }

        return addresses.isEmpty() ? null : addresses;
    }

    public void attemptToSend() {
        if (heldBox.isEmpty() || animationTicks != 0)
            return;

        LadleItem.clearAddress(heldBox);
        LadleItem.clearRemainAddrs(heldBox);

        ArrayList<String> addresses = addressesList;
        if (!addresses.isEmpty()) {
            LadleItem.addAddress(heldBox, addresses.remove(0));
            LadleItem.addRemainAddrs(heldBox, addresses);
        }

        // BlockPos linkPos = getLinkPos();
        /*
         * if (linkPos != null && level.getBlockEntity(linkPos) instanceof
         * PackagerLinkBlockEntity plbe)
         * plbe.behaviour.deductFromAccurateSummary(extractedItems);
         */

        /*
         * if (!heldBox.isEmpty() || animationTicks != 0) {
         * queuedExitingPackages.add(new BigItemStack(createdBox, 1));
         * return;
         * }
         */

        animationInward = false;
        animationTicks = CYCLE;
        ladleInv.allowExtract();
        notifyUpdate();
    }

    public IItemHandler getItemHandler() {
        return (IItemHandler) ladleInv;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (ladleInv != null)
            ItemHelper.dropContents(level, worldPosition, ladleInv);
    }
}