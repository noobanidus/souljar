package noobanidus.mods.souljar;

import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import noobanidus.libs.noobutil.registrate.CustomRegistrate;
import noobanidus.mods.souljar.init.ModItems;
import noobanidus.mods.souljar.init.ModLang;
import noobanidus.mods.souljar.init.ModSounds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("souljar")
public class SoulJar {
  public static final Logger LOG = LogManager.getLogger();
  public static final String MODID = "souljar";

  public static CustomRegistrate REGISTRATE;

  public static final ItemGroup ITEM_GROUP = new ItemGroup("souljar") {
    @Override
    public ItemStack createIcon() {
      return new ItemStack(ModItems.SOUL_JAR.get());
    }
  };

  public SoulJar() {
    REGISTRATE = CustomRegistrate.create(MODID);
    REGISTRATE.itemGroup(NonNullSupplier.of(() -> ITEM_GROUP));

    ModItems.load();
    ModSounds.load();
    ModLang.load();
  }
}
