package noobanidus.mods.threadsafety;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("threadsafety")
public class ThreadSafety {
  public static final Logger LOG = LogManager.getLogger();
  public static final String MODID = "threadsafety";

  public ThreadSafety() {
    IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
  }
}
