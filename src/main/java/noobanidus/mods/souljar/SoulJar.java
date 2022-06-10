package noobanidus.mods.souljar;

import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import noobanidus.libs.noobutil.registrate.CustomRegistrate;
import noobanidus.mods.souljar.config.ConfigManager;
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

  public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab("souljar") {
    @Override
    public ItemStack makeIcon() {
      return new ItemStack(ModItems.SOUL_JAR.get());
    }
  };

  public SoulJar() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.COMMON_CONFIG);
    ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));

    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    bus.register(ConfigManager.class);

    REGISTRATE = CustomRegistrate.create(MODID);
    REGISTRATE.creativeModeTab(NonNullSupplier.of(() -> ITEM_GROUP));

    ModItems.load();
    ModSounds.load();
    ModLang.load();
  }
}
