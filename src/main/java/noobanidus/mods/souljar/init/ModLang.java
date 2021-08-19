package noobanidus.mods.souljar.init;

import com.tterrag.registrate.providers.ProviderType;

import static noobanidus.mods.souljar.SoulJar.REGISTRATE;

public class ModLang {
  static {
    REGISTRATE.addDataGenerator(ProviderType.LANG, (p) -> {
      p.add("souljar.listing", "Entity %s: %s");
      p.add("itemGroup.souljar", "Soul Jar");
      p.add("souljar.tooltip1", "Right-Click entity to store it in the jar.");
      p.add("souljar.tooltip2", "Right-Click a block to release the most recently stored entity.");
    });
  }

  public static void load () {

  }
}
