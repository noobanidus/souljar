package noobanidus.mods.souljar.events;

import com.google.common.collect.Sets;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.souljar.SoulJar;
import noobanidus.mods.souljar.init.ModItems;

import java.util.Set;

@Mod.EventBusSubscriber(modid = SoulJar.MODID)
public class HorseShitHandler {
  private static final Set<EntityType<?>> HORSES = Sets.newHashSet(EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.ZOMBIE_HORSE, EntityType.SKELETON_HORSE, EntityType.VILLAGER, EntityType.LLAMA, EntityType.TRADER_LLAMA, EntityType.WANDERING_TRADER, EntityType.PIG, EntityType.STRIDER);

  @SubscribeEvent
  public static void onRightClick(PlayerInteractEvent.EntityInteract event) {
    if (event.getTarget().getCommandSenderWorld().isClientSide) {
      return;
    }

    ItemStack stack;
    Hand hand;

    if (ModItems.SOUL_JAR.get().equals(event.getPlayer().getMainHandItem().getItem())) {
      hand = Hand.MAIN_HAND;
      stack = event.getPlayer().getMainHandItem();
    } else if (ModItems.SOUL_JAR.get().equals(event.getPlayer().getOffhandItem().getItem())) {
      hand = Hand.OFF_HAND;
      stack = event.getPlayer().getOffhandItem();
    } else {
      return;
    }

    if (!HORSES.contains(event.getTarget().getType())) {
      return;
    }

    ActionResultType result = ModItems.SOUL_JAR.get().interactLivingEntity(stack, event.getPlayer(), (LivingEntity) event.getTarget(), hand);
    event.setCanceled(true);
    event.setCancellationResult(result);
  }
}
