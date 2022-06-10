package noobanidus.mods.souljar.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noobanidus.mods.souljar.config.ConfigManager;
import noobanidus.mods.souljar.init.ModSounds;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class SoulJarItem extends Item {
  public SoulJarItem(Properties properties) {
    super(properties);
  }

  @Override
  public boolean isFoil(ItemStack stack) {
    return !getEntityList(stack).isEmpty();
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
    return true;
  }

  @Override
  public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
    if (playerIn.level.isClientSide) {
      return InteractionResult.SUCCESS;
    }

    stack = playerIn.getItemInHand(hand);
    boolean cab = isCab(stack);

    BlockPos pos = target.blockPosition();

    if (!(target instanceof Player)) {
      ListTag entityList = getEntityList(stack);
      if (entityList.size() == 4) {
        playerIn.level.playSound(null, pos, cab ? ModSounds.CAB_FULL.get() : ModSounds.FULL.get(), SoundSource.PLAYERS, cab ? 0.8f : 0.3f, 1f);
        return InteractionResult.FAIL;
      }

      if (!ConfigManager.canPickup(target)) {
        playerIn.level.playSound(null, pos, cab ? ModSounds.CAB_FULL.get() : ModSounds.FULL.get(), SoundSource.PLAYERS, cab ? 0.8f : 0.3f, 1f);
        return InteractionResult.FAIL;
      }

      target.stopRiding();
      target.ejectPassengers();
      CompoundTag entity = new CompoundTag();
      target.save(entity);
      entityList.add(entity);
      target.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
      saveEntityList(stack, entityList);

      playerIn.level.playSound(null, pos, cab ? ModSounds.CAB_PICKUP.get() : ModSounds.PICKUP.get(), SoundSource.PLAYERS, cab ? 0.8f : 0.3f, 1f);

      return InteractionResult.SUCCESS;
    } else {
      playerIn.level.playSound(null, pos, cab ? ModSounds.CAB_FULL.get() : ModSounds.FULL.get(), SoundSource.PLAYERS, cab ? 0.8f : 0.3f, 1f);
      return InteractionResult.FAIL;
    }
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level world = context.getLevel();
    if (!world.isClientSide) {
      Player player = context.getPlayer();
      BlockPos position = context.getClickedPos().relative(context.getClickedFace());
      if (player == null) {
        world.playSound(null, position, ModSounds.FULL.get(), SoundSource.PLAYERS, 0.25f, 1f);
        return InteractionResult.FAIL;
      }
      ItemStack stack = player.getItemInHand(context.getHand());
      boolean cab = isCab(stack);
      ListTag entityList = getEntityList(stack);
      if (entityList.isEmpty()) {
        world.playSound(null, position, cab ? ModSounds.CAB_FULL.get() : ModSounds.FULL.get(), SoundSource.PLAYERS, cab ? 0.8f : 0.3f, 1f);
        return InteractionResult.FAIL;
      }

      int index = entityList.size() - 1;
      CompoundTag data = entityList.getCompound(index);
      entityList.remove(index);
      saveEntityList(stack, entityList);
      Entity result = EntityType.loadEntityRecursive(data, world, o -> o);
      if (result != null) {
        result.setPos(position.getX() + 0.5, position.getY(), position.getZ() + 0.5);
        world.addFreshEntity(result);
        world.playSound(null, position, cab ? ModSounds.CAB_RELEASE.get() : ModSounds.RELEASE.get(), SoundSource.PLAYERS, cab ? 0.8f : 0.3f, 1f);
        return InteractionResult.SUCCESS;
      }
    }
    return super.useOn(context);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    super.appendHoverText(stack, worldIn, tooltip, flagIn);
    tooltip.add(new TextComponent(""));
    tooltip.add(new TranslatableComponent("souljar.tooltip1").setStyle(Style.EMPTY.applyFormat(ChatFormatting.LIGHT_PURPLE)));
    tooltip.add(new TranslatableComponent("souljar.tooltip2").setStyle(Style.EMPTY.applyFormat(ChatFormatting.LIGHT_PURPLE)));
    ListTag  list = getEntityList(stack);
    if (!list.isEmpty()) {
      tooltip.add(new TextComponent(""));
      int i = 1;
      for (Tag item : list) {
        tooltip.add(new TranslatableComponent("souljar.listing", i++, EntityType.by((CompoundTag) item).map(EntityType::getDescription).orElse(new TextComponent("Unknown"))));
      }
    }
  }

  public static ListTag getEntityList(ItemStack stack) {
    CompoundTag tag = stack.getOrCreateTag();
    if (tag.contains(Identifiers.ENTITIES, Tag.TAG_LIST)) {
      return tag.getList(Identifiers.ENTITIES, Tag.TAG_COMPOUND);
    } else {
      ListTag result = new ListTag();
      tag.put(Identifiers.ENTITIES, result);
      return result;
    }
  }

  public static void saveEntityList(ItemStack stack, ListTag list) {
    CompoundTag tag = stack.getOrCreateTag();
    tag.put(Identifiers.ENTITIES, list);
  }

  public static boolean isCab(ItemStack stack) {
    if (stack.hasCustomHoverName()) {
      String name = stack.getHoverName().getString().toLowerCase(Locale.ROOT);
      return name.contains("cab");
    }
    return false;
  }

  private static class Identifiers {
    public static String ENTITIES = "entities";
  }
}
