package noobanidus.mods.souljar.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import noobanidus.mods.souljar.SoulJar;

import java.nio.file.Path;
import java.util.*;

public class ConfigManager {
  private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

  public static ForgeConfigSpec COMMON_CONFIG;

  private final static ForgeConfigSpec.ConfigValue<List<? extends String>> mob_whitelist;
  private final static ForgeConfigSpec.ConfigValue<List<? extends String>> mob_blacklist;
  private final static ForgeConfigSpec.ConfigValue<List<? extends String>> mod_mob_whitelist;
  private final static ForgeConfigSpec.ConfigValue<List<? extends String>> mod_mob_blacklist;
  private final static ForgeConfigSpec.DoubleValue health_maximum;
  private final static ForgeConfigSpec.BooleanValue disable_bosses;

  private static Set<EntityType<?>> mobBlacklist = null;
  private static Set<EntityType<?>> mobWhitelist = null;
  private static Set<String> modBlacklist = null;
  private static Set<String> modWhitelist = null;

  public static void loadConfig(ForgeConfigSpec spec, Path path) {
    CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
    configData.load();
    spec.setConfig(configData);
  }

  @SubscribeEvent
  public static void configReloaded(ModConfig.ModConfigEvent event) {
    mobBlacklist = null;
    mobWhitelist = null;
    modBlacklist = null;
    modWhitelist = null;
  }

  public static Set<EntityType<?>> getMobBlacklist() {
    if (mobBlacklist == null) {
      mobBlacklist = new HashSet<>();
      for (String s : mob_blacklist.get()) {
        EntityType<?> e = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(s));
        if (e == null) {
          SoulJar.LOG.error("Invalid entity for blacklist: " + s);
        } else {
          mobBlacklist.add(e);
        }
      }
    }
    return mobBlacklist;
  }

  public static Set<EntityType<?>> getMobWhitelist() {
    if (mobWhitelist == null) {
      mobWhitelist = new HashSet<>();
      for (String s : mob_whitelist.get()) {
        EntityType<?> e = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(s));
        if (e == null) {
          SoulJar.LOG.error("Invalid entity for whitelist: " + s);
        } else {
          mobWhitelist.add(e);
        }
      }
    }
    return mobWhitelist;
  }

  public static Set<String> getModWhitelist() {
    if (modWhitelist == null) {
      modWhitelist = new HashSet<>(mod_mob_whitelist.get());
    }
    return modWhitelist;
  }

  public static Set<String> getModBlacklist() {
    if (modBlacklist == null) {
      modBlacklist = new HashSet<>(mod_mob_blacklist.get());
    }
    return modBlacklist;
  }

  static {
    COMMON_BUILDER.push("souljar configuration");
    mob_blacklist = COMMON_BUILDER.comment("list of mobs to blacklist (by entity type as a resource location, i.e., [\"minecraft:cow\", \"minecraft:chicken\"]").defineList("mob_blacklist", Collections.emptyList(), o -> o instanceof String && ((String) o).contains(":"));
    mod_mob_blacklist = COMMON_BUILDER.comment("list of mod names to blacklist (specifying an empty blacklist allows any mod, specifying any mod prevents any mobs from that mod)").defineList("mod_blacklist", Collections.emptyList(), o -> o instanceof String);
    mob_whitelist = COMMON_BUILDER.comment("list of mobs to whitelist (by entity type as a resource location, i.e., [\"minecraft:cow\", \"minecraft:chicken\"]").defineList("mob_blacklist", Collections.emptyList(), o -> o instanceof String && ((String) o).contains(":"));
    mod_mob_whitelist = COMMON_BUILDER.comment("list of mod names to whitelist (specifying an empty whitelist allows any mob; specifying any mod only allows those mods)").defineList("mod_whitelist", Collections.emptyList(), o -> o instanceof String);
    health_maximum = COMMON_BUILDER.comment("entities with this many hearts or more are blocked from being picked up by the soul jar (-1.0 to disable)").defineInRange("health_maximum", -1.0, -1.0, Double.MAX_VALUE);
    disable_bosses = COMMON_BUILDER.comment("whether or not 'boss' entities should be allowed to be picked up").define("disable_boss_entities", true);
    COMMON_CONFIG = COMMON_BUILDER.build();
  }

  public static boolean canPickup(Entity entity) {
    if (!entity.canChangeDimensions() && disable_bosses.get()) {
      return false;
    }

    EntityType<?> type = entity.getType();
    if (getMobBlacklist().contains(type)) {
      return false;
    }

    if (getMobWhitelist().contains(type)) {
      return true;
    }

    if (!getMobWhitelist().isEmpty()) {
      return false;
    }

    String namespace = Objects.requireNonNull(type.getRegistryName()).getNamespace();
    if (getModBlacklist().contains(namespace)) {
      return false;
    }

    if (getModWhitelist().contains(namespace)) {
      return true;
    }

    if (!getModWhitelist().isEmpty()) {
      return false;
    }

    if (entity instanceof LivingEntity) {
      LivingEntity living = (LivingEntity) entity;
      float health = ((Number) health_maximum.get()).floatValue();
      return health == -1 || living.getMaxHealth() < health;
    }

    return true;
  }
}
