package noobanidus.mods.souljar.init;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Rarity;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import noobanidus.mods.souljar.SoulJar;
import noobanidus.mods.souljar.items.SoulJarItem;

import static noobanidus.mods.souljar.SoulJar.REGISTRATE;

public class ModItems {

  public static RegistryEntry<SoulJarItem> SOUL_JAR = REGISTRATE.item("soul_jar", SoulJarItem::new)
      .properties(o -> o.rarity(Rarity.EPIC)
          .stacksTo(1))
      .model(NonNullBiConsumer.noop())
      .recipe((ctx, p) -> {
        ShapedRecipeBuilder.shaped(ctx.getEntry(), 1)
            .pattern("GHG")
            .pattern("GSG")
            .pattern("GGG")
            .define('G', Tags.Items.GLASS)
            .define('H', Tags.Items.HEADS)
            .define('S', ItemTags.SOUL_FIRE_BASE_BLOCKS)
            .unlockedBy("has_head", RegistrateRecipeProvider.hasItem(Tags.Items.HEADS))
            .save(p, new ResourceLocation(SoulJar.MODID, "soul_jar"));
      })
      .register();

  public static void load() {
  }
}
