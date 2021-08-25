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
          .maxStackSize(1))
      .model(NonNullBiConsumer.noop())
      .recipe((ctx, p) -> {
        ShapedRecipeBuilder.shapedRecipe(ctx.getEntry(), 1)
            .patternLine("GHG")
            .patternLine("GSG")
            .patternLine("GGG")
            .key('G', Tags.Items.GLASS)
            .key('H', Tags.Items.HEADS)
            .key('S', ItemTags.SOUL_FIRE_BASE_BLOCKS)
            .addCriterion("has_head", RegistrateRecipeProvider.hasItem(Tags.Items.HEADS))
            .build(p, new ResourceLocation(SoulJar.MODID, "soul_jar"));
      })
      .register();

  public static void load() {
  }
}
