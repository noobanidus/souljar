package noobanidus.mods.souljar.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import noobanidus.mods.souljar.config.ConfigManager;
import noobanidus.mods.souljar.init.ModSounds;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

import net.minecraft.item.Item.Properties;

public class SoulJarItem extends Item {
  public SoulJarItem(Properties properties) {
    super(properties);
  }

  @Override
  public boolean isFoil(ItemStack stack) {
    return !getEntityList(stack).isEmpty();
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
    return true;
  }

  @Override
  public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
    if (playerIn.level.isClientSide) {
      return ActionResultType.CONSUME;
    }

    stack = playerIn.getItemInHand(hand);
    boolean cab = isCab(stack);

    BlockPos pos = target.blockPosition();

    if (!(target instanceof PlayerEntity)) {
      ListNBT entityList = getEntityList(stack);
      if (entityList.size() == 4) {
        playerIn.level.playSound(null, pos, cab ? ModSounds.CAB_FULL.get() : ModSounds.FULL.get(), SoundCategory.PLAYERS, cab ? 0.8f : 0.3f, 1f);
        return ActionResultType.FAIL;
      }

      if (!ConfigManager.canPickup(target)) {
        playerIn.level.playSound(null, pos, cab ? ModSounds.CAB_FULL.get() : ModSounds.FULL.get(), SoundCategory.PLAYERS, cab ? 0.8f : 0.3f, 1f);
        return ActionResultType.FAIL;
      }

      target.stopRiding();
      target.ejectPassengers();
      CompoundNBT entity = new CompoundNBT();
      target.save(entity);
      entityList.add(entity);
      target.remove();
      saveEntityList(stack, entityList);

      playerIn.level.playSound(null, pos, cab ? ModSounds.CAB_PICKUP.get() : ModSounds.PICKUP.get(), SoundCategory.PLAYERS, cab ? 0.8f : 0.3f, 1f);

      return ActionResultType.SUCCESS;
    } else {
      playerIn.level.playSound(null, pos, cab ? ModSounds.CAB_FULL.get() : ModSounds.FULL.get(), SoundCategory.PLAYERS, cab ? 0.8f : 0.3f, 1f);
      return ActionResultType.FAIL;
    }
  }

  @Override
  public ActionResultType useOn(ItemUseContext context) {
    World world = context.getLevel();
    if (!world.isClientSide) {
      PlayerEntity player = context.getPlayer();
      BlockPos position = context.getClickedPos().relative(context.getClickedFace());
      if (player == null) {
        world.playSound(null, position, ModSounds.FULL.get(), SoundCategory.PLAYERS, 0.25f, 1f);
        return ActionResultType.FAIL;
      }
      ItemStack stack = player.getItemInHand(context.getHand());
      boolean cab = isCab(stack);
      ListNBT entityList = getEntityList(stack);
      if (entityList.isEmpty()) {
        world.playSound(null, position, cab ? ModSounds.CAB_FULL.get() : ModSounds.FULL.get(), SoundCategory.PLAYERS, cab ? 0.8f : 0.3f, 1f);
        return ActionResultType.FAIL;
      }

      int index = entityList.size() - 1;
      CompoundNBT data = entityList.getCompound(index);
      entityList.remove(index);
      saveEntityList(stack, entityList);
      Entity result = EntityType.loadEntityRecursive(data, world, o -> o);
      if (result != null) {
        result.setPos(position.getX() + 0.5, position.getY(), position.getZ() + 0.5);
        world.addFreshEntity(result);
        world.playSound(null, position, cab ? ModSounds.CAB_RELEASE.get() : ModSounds.RELEASE.get(), SoundCategory.PLAYERS, cab ? 0.8f : 0.3f, 1f);
        return ActionResultType.SUCCESS;
      }
    }
    return super.useOn(context);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    super.appendHoverText(stack, worldIn, tooltip, flagIn);
    tooltip.add(new StringTextComponent(""));
    tooltip.add(new TranslationTextComponent("souljar.tooltip1").setStyle(Style.EMPTY.applyFormat(TextFormatting.LIGHT_PURPLE)));
    tooltip.add(new TranslationTextComponent("souljar.tooltip2").setStyle(Style.EMPTY.applyFormat(TextFormatting.LIGHT_PURPLE)));
    ListNBT list = getEntityList(stack);
    if (!list.isEmpty()) {
      tooltip.add(new StringTextComponent(""));
      int i = 1;
      for (INBT item : list) {
        tooltip.add(new TranslationTextComponent("souljar.listing", i++, EntityType.by((CompoundNBT) item).map(EntityType::getDescription).orElse(new StringTextComponent("Unknown"))));
      }
    }
  }

  public static ListNBT getEntityList(ItemStack stack) {
    CompoundNBT tag = stack.getOrCreateTag();
    if (tag.contains(Identifiers.ENTITIES, Constants.NBT.TAG_LIST)) {
      return tag.getList(Identifiers.ENTITIES, Constants.NBT.TAG_COMPOUND);
    } else {
      ListNBT result = new ListNBT();
      tag.put(Identifiers.ENTITIES, result);
      return result;
    }
  }

  public static void saveEntityList(ItemStack stack, ListNBT list) {
    CompoundNBT tag = stack.getOrCreateTag();
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
