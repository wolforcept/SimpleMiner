package pt.wolforce.simpleminer;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(SimpleMiner.MODID)
public class SimpleMiner {

    public static final String MODID = "simpleminer";

    public static final ItemGroup CREATIVE_TAB = new ItemGroup(MODID + "_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(MINER_BLOCK.get().asItem());
        }
    };

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);

    public static final RegistryObject<Block> MINER_BLOCK = BLOCKS.register("miner", BlockMiner::new);
    public static final RegistryObject<Block> SPEED_BLOCK = BLOCKS.register("speed", () -> new BlockExtra(0));
    public static final RegistryObject<Block> AREA_BLOCK = BLOCKS.register("area", () -> new BlockExtra(1));
    public static final RegistryObject<Block> EFFICIENCY_BLOCK = BLOCKS.register("efficiency", () -> new BlockExtra(2));

    public static final RegistryObject<Item> CRYSTAL_ITEM = ITEMS.register("crystal", ItemCrystal::new);
    public static final RegistryObject<Item> MINER_ITEM = ITEMS.register("miner", () -> //
            new BlockItemWithDescription(MINER_BLOCK.get(), "Requires an inventory adjacent or diagonally, with mining crystal inside."));
    public static final RegistryObject<Item> SPEED_ITEM = ITEMS.register("speed", () -> //
            new BlockItemWithDescription(SPEED_BLOCK.get(), "Place adjacent to the miner to boost its speed."));
    public static final RegistryObject<Item> AREA_ITEM = ITEMS.register("area", () -> //
            new BlockItemWithDescription(AREA_BLOCK.get(), "Place adjacent to the miner to boost its range."));
    public static final RegistryObject<Item> EFFICIENCY_ITEM = ITEMS.register("efficiency", () -> //
            new BlockItemWithDescription(EFFICIENCY_BLOCK.get(), "Place adjacent or diagonal to the miner to boost its efficiency.", "(10% chance to not consume mining crystal per efficiency)"));

    public static final RegistryObject<TileEntityType<TileEntityMiner>> MINER_TILE_ENTITY = //
            TILE_ENTITIES.register("miner", () -> TileEntityType.Builder.of(TileEntityMiner::new, MINER_BLOCK.get()).build(null));

    public SimpleMiner() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);
        this.configSetup();
    }

    private void configSetup() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));
    }

}
