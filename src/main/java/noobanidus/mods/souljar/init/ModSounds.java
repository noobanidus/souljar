package noobanidus.mods.souljar.init;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.sounds.SoundEvent;

import static noobanidus.mods.souljar.SoulJar.REGISTRATE;

public class ModSounds {
  public static final RegistryEntry<SoundEvent> PICKUP = REGISTRATE.soundEvent("souljar.pickup").register();
  public static final RegistryEntry<SoundEvent> RELEASE = REGISTRATE.soundEvent("souljar.release").register();
  public static final RegistryEntry<SoundEvent> FULL = REGISTRATE.soundEvent("souljar.full").register();

  public static final RegistryEntry<SoundEvent> CAB_PICKUP = REGISTRATE.soundEvent("soulcab.pickup").register();
  public static final RegistryEntry<SoundEvent> CAB_RELEASE = REGISTRATE.soundEvent("soulcab.release").register();
  public static final RegistryEntry<SoundEvent> CAB_FULL = REGISTRATE.soundEvent("soulcab.full").register();

  public static void load () {

  }
}
