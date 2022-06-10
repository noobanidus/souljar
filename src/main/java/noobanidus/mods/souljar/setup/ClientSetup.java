package noobanidus.mods.souljar.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import noobanidus.mods.souljar.SoulJar;
import noobanidus.mods.souljar.init.ModItems;
import noobanidus.mods.souljar.items.SoulJarItem;

@Mod.EventBusSubscriber(value= Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = SoulJar.MODID)
public class ClientSetup {
  @SubscribeEvent
  public static void clientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      ItemProperties.register(ModItems.SOUL_JAR.get(), new ResourceLocation("souls"), (stack, world, entity, flag) -> SoulJarItem.getEntityList(stack).size());
      Minecraft.getInstance().getItemColors().register(new ItemColor() {
        @Override
        public int getColor(ItemStack p_getColor_1_, int p_getColor_2_) {
          if (p_getColor_2_ == 1) {
            int count = SoulJarItem.getEntityList(p_getColor_1_).size();
            switch (count) {
              case 0:
                return -1;
              case 1:
                return DyeColor.CYAN.getTextColor();
              case 2:
                return DyeColor.LIGHT_BLUE.getTextColor();
              case 3:
                return DyeColor.MAGENTA.getTextColor();
              case 4:
                return DyeColor.PURPLE.getTextColor();
            }
          }
          return -1;
        }
      }, ModItems.SOUL_JAR.get());
      ItemProperties.register(ModItems.SOUL_JAR.get(), new ResourceLocation("cab"), (stack, world, entity, flag) -> SoulJarItem.isCab(stack) ? 1 : 0);
    });
  }
}
