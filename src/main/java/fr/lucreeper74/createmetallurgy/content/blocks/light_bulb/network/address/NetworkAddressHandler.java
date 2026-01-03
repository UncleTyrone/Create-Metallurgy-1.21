package fr.lucreeper74.createmetallurgy.content.blocks.light_bulb.network.address;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.RaycastHelper;
import fr.lucreeper74.createmetallurgy.CreateMetallurgy;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

@EventBusSubscriber(modid = CreateMetallurgy.MOD_ID)
public class NetworkAddressHandler {

    @SubscribeEvent
    public static void onBlockActivated(UseItemOnBlockEvent event) {
        UseOnContext context = event.getUseOnContext();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        InteractionHand hand = context.getHand();

        if (player.isShiftKeyDown() || player.isSpectator())
            return;

        NetworkAddressBehaviour behaviour = BlockEntityBehaviour.get(world, pos, NetworkAddressBehaviour.TYPE);
        if (behaviour == null)
            return;

        ItemStack heldItem = player.getItemInHand(hand);
        BlockHitResult ray = RaycastHelper.rayTraceRange(world, player, 10);
        if (ray == null)
            return;
        if (AllItems.LINKED_CONTROLLER.isIn(heldItem))
            return;
        if (AllItems.WRENCH.isIn(heldItem))
            return;

        boolean fakePlayer = player instanceof FakePlayer;

        if (behaviour.testHit(ray.getLocation()) || fakePlayer) {
            if (!world.isClientSide())
                behaviour.setAddress(heldItem);
            event.cancelWithResult(ItemInteractionResult.SUCCESS);
            world.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, .25f, .1f);
        }
    }
}
