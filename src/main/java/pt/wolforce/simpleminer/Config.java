package pt.wolforce.simpleminer;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber
public class Config {

    private static final Builder COMMON_BUILDER = new Builder();
    public static final ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.ConfigValue<Integer> BASE_AREA = COMMON_BUILDER.comment("The minimum size of the excavated area").define("base_area", 4);
    public static ForgeConfigSpec.ConfigValue<Integer> AREA_UPGRADE = COMMON_BUILDER.comment("The size increase of area upgrade").define("area_upgrade", 1);
    public static ForgeConfigSpec.ConfigValue<Integer> BASE_SPEED = COMMON_BUILDER.comment("The time it takes to mine a block").define("base_speed", 200);
    public static ForgeConfigSpec.ConfigValue<Integer> SPEED_UPGRADE = COMMON_BUILDER.comment("The time reduction of each speed upgrade").define("speed_upgrade", 40);
    public static ForgeConfigSpec.ConfigValue<Float> EFFICIENCY_CHANCE = COMMON_BUILDER.comment("The probability of not consuming fuel of each efficiency upgrade").define("efficiency_upgrade", .1f);
    public static ForgeConfigSpec.ConfigValue<Integer> CRYSTAL_SIZE = COMMON_BUILDER.comment("The durability of mining crystals.").define("crystal_size", 512);
    public static ForgeConfigSpec.ConfigValue<Integer> CUTOFF_TIME = COMMON_BUILDER.comment("The maximum time it should take to break a block before it starts reducing it.").define("cutoff_time", 100);
    public static ForgeConfigSpec.ConfigValue<Float> CUTOFF_VALUE = COMMON_BUILDER.comment("How much to reduce after the cutoff.").define("cutoff_value", 0.1f);

    static {
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();

        configData.load();
        spec.setConfig(configData);
    }
}