package noobanidus.mods.souljar.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
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
import noobanidus.mods.souljar.init.ModSounds;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class SoulJarItem extends Item {
  public SoulJarItem(Properties properties) {
    super(properties);
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return !getEntityList(stack).isEmpty();
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
    return true;
  }

  @Override
  public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
    if (playerIn.world.isRemote) {
      return ActionResultType.CONSUME;
    }

    stack = playerIn.getHeldItem(hand);

    BlockPos pos = target.getPosition();

    if (!(target instanceof PlayerEntity)) {
      ListNBT entityList = getEntityList(stack);
      if (entityList.size() == 4) {
        playerIn.world.playSound(null, pos, ModSounds.FULL.get(), SoundCategory.PLAYERS, 0.25f, 1f);
        return ActionResultType.FAIL;
      }

      target.stopRiding();
      target.removePassengers();
      CompoundNBT entity = new CompoundNBT();
      target.writeUnlessPassenger(entity);
      entityList.add(entity);
      target.remove();
      saveEntityList(stack, entityList);

      playerIn.world.playSound(null, pos, ModSounds.PICKUP.get(), SoundCategory.PLAYERS, 0.25f, 1f);

      return ActionResultType.SUCCESS;
    } else {
      playerIn.world.playSound(null, pos, ModSounds.FULL.get(), SoundCategory.PLAYERS, 0.25f, 1f);
      return ActionResultType.FAIL;
    }
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    World world = context.getWorld();
    if (!world.isRemote) {
      PlayerEntity player = context.getPlayer();
      BlockPos position = context.getPos().offset(context.getFace());
      if (player == null) {
        world.playSound(null, position, ModSounds.FULL.get(), SoundCategory.PLAYERS, 0.25f, 1f);
        return ActionResultType.FAIL;
      }
      ItemStack stack = player.getHeldItem(context.getHand());
      ListNBT entityList = getEntityList(stack);
      if (entityList.isEmpty()) {
        world.playSound(null, position, ModSounds.FULL.get(), SoundCategory.PLAYERS, 0.25f, 1f);
        return ActionResultType.FAIL;
      }

      int index = entityList.size() - 1;
      CompoundNBT data = entityList.getCompound(index);
      entityList.remove(index);
      saveEntityList(stack, entityList);
      Entity result = EntityType.loadEntityAndExecute(data, world, o -> o);
      if (result != null) {
        result.setPosition(position.getX() + 0.5, position.getY(), position.getZ() + 0.5);
        world.addEntity(result);
        world.playSound(null, position, ModSounds.RELEASE.get(), SoundCategory.PLAYERS, 0.25f, 1f);
        return ActionResultType.SUCCESS;
      }
    }
    return super.onItemUse(context);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    tooltip.add(new StringTextComponent(""));
    tooltip.add(new TranslationTextComponent("souljar.tooltip1").setStyle(Style.EMPTY.applyFormatting(TextFormatting.LIGHT_PURPLE)));
    tooltip.add(new TranslationTextComponent("souljar.tooltip2").setStyle(Style.EMPTY.applyFormatting(TextFormatting.LIGHT_PURPLE)));
    ListNBT list = getEntityList(stack);
    if (!list.isEmpty()) {
      tooltip.add(new StringTextComponent(""));
      int i = 1;
      for (INBT item : list) {
        tooltip.add(new TranslationTextComponent("souljar.listing", i++, EntityType.readEntityType((CompoundNBT) item).map(EntityType::getName).orElse(new StringTextComponent("Unknown"))));
      }
    }
  }

  public static ListNBT getEntityList (ItemStack stack) {
    CompoundNBT tag = stack.getOrCreateTag();
    if (tag.contains(Identifiers.ENTITIES, Constants.NBT.TAG_LIST)) {
      return tag.getList(Identifiers.ENTITIES, Constants.NBT.TAG_COMPOUND);
    } else {
      ListNBT result = new ListNBT();
      tag.put(Identifiers.ENTITIES, result);
      return result;
    }
  }

  public static void saveEntityList (ItemStack stack, ListNBT list) {
    CompoundNBT tag = stack.getOrCreateTag();
    tag.put(Identifiers.ENTITIES, list);
  }

  private static class Identifiers {
    public static String ENTITIES = "entities";
  }
}
