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
      p.add("souljar.subtitles.pickup", "Jarred a creature");
      p.add("souljar.subtitles.release", "Unjarred a creature");
      p.add("souljar.subtitles.full", "Jar is full");
      p.add("soulcab.subtitles.pickup", "Picked up a passenger");
      p.add("soulcab.subtitles.release", "Dropped off a passenger");
      p.add("soulcab.subtitles.full", "Cab is full");
    });
  }

  public static void load () {

  }
}
