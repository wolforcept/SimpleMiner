package pt.wolforce.simpleminer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static ModConfigSpec.ConfigValue<Integer> BASE_AREA = BUILDER.comment("The minimum size of the excavated area").define("base_area", 4);
    public static ModConfigSpec.ConfigValue<Integer> AREA_UPGRADE = BUILDER.comment("The size increase of area upgrade").define("area_upgrade", 1);
    public static ModConfigSpec.ConfigValue<Integer> BASE_SPEED = BUILDER.comment("The time it takes to mine a block").define("base_speed", 200);
    public static ModConfigSpec.ConfigValue<Integer> SPEED_UPGRADE = BUILDER.comment("The time reduction of each speed upgrade").define("speed_upgrade", 40);
    public static ModConfigSpec.ConfigValue<Float> EFFICIENCY_CHANCE = BUILDER.comment("The probability of not consuming fuel of each efficiency upgrade").define("efficiency_upgrade", .1f);
    public static ModConfigSpec.ConfigValue<Integer> CRYSTAL_SIZE = BUILDER.comment("The durability of mining crystals.").define("crystal_size", 512);
    public static ModConfigSpec.ConfigValue<Integer> CUTOFF_TIME = BUILDER.comment("The maximum time it should take to break a block before it starts reducing it.").define("cutoff_time", 100);
    public static ModConfigSpec.ConfigValue<Float> CUTOFF_VALUE = BUILDER.comment("How much to reduce after the cutoff.").define("cutoff_value", 0.1f);
    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
