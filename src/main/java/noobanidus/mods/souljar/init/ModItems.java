package noobanidus.mods.souljar.init;

import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.item.Rarity;
import noobanidus.mods.souljar.items.SoulJarItem;

import static noobanidus.mods.souljar.SoulJar.REGISTRATE;

public class ModItems {

  public static RegistryEntry<SoulJarItem> SOUL_JAR = REGISTRATE.item("soul_jar", SoulJarItem::new)
      .properties(o -> o.rarity(Rarity.EPIC)
          .maxStackSize(1))
      .model(NonNullBiConsumer.noop())
      .register();

  public static void load() {
  }
}
