package pt.wolforce.simpleminer;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SimpleMiner.MODID)
public class SimpleMiner {
    public static final String MODID = "simpleminer";
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredBlock<Block> MINER_BLOCK = BLOCKS.registerBlock("miner", MinerBlock::new);
    public static final DeferredItem<BlockItem> MINER_ITEM = ITEMS.registerItem("miner", (props) ->
            new BlockItemWithDescription(MINER_BLOCK.get(), props, "Requires an inventory adjacent or diagonally, with mining crystal inside."), Item.Properties::useBlockDescriptionPrefix);

    public static final DeferredBlock<Block> SPEED_BLOCK = BLOCKS.registerBlock("speed", (p) -> new BlockExtra(p, ShapesEnum.SPEED));
    public static final DeferredItem<BlockItem> SPEED_ITEM = ITEMS.registerItem("speed", (props) ->
            new BlockItemWithDescription(SPEED_BLOCK.get(), props,"Place adjacent to the miner to boost its speed."), Item.Properties::useBlockDescriptionPrefix);

    public static final DeferredBlock<Block> AREA_BLOCK = BLOCKS.registerBlock("area",  p -> new BlockExtra(p, ShapesEnum.AREA));
    public static final DeferredItem<BlockItem> AREA_ITEM = ITEMS.registerItem("area", (props) ->
            new BlockItemWithDescription(AREA_BLOCK.get(), props,"Place adjacent to the miner to boost its range."), Item.Properties::useBlockDescriptionPrefix);

    public static final DeferredBlock<Block> EFFICIENCY_BLOCK = BLOCKS.registerBlock("efficiency", (p) -> new BlockExtra(p, ShapesEnum.EFFICIENCY));
    public static final DeferredItem<BlockItem> EFFICIENCY_ITEM = ITEMS.registerItem("efficiency", (props) ->
            new BlockItemWithDescription(EFFICIENCY_BLOCK.get(), props,"Place adjacent or diagonal to the miner to boost its efficiency.", "(10% chance to not consume mining crystal per efficiency)"), Item.Properties::useBlockDescriptionPrefix);
    public static final DeferredItem<Item> CRYSTAL_ITEM = ITEMS.registerItem("crystal", CrystalItem::new);

    public static final Supplier<BlockEntityType<MinerBlockEntity>> MINER_BLOCK_ENTITY = BLOCK_ENTITIES.register("miner", () -> new BlockEntityType<>(MinerBlockEntity::new, false, MINER_BLOCK.get()));

    // Creates a creative tab with the id "simpleminer:creative_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("creative_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.simpleminer")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> new ItemStack(MINER_ITEM.get()))
            .displayItems((parameters, output) -> {
                ITEMS.getEntries().forEach(entry -> output.accept(entry.get()));
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public SimpleMiner(IEventBus modEventBus, ModContainer modContainer) {
        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        BLOCK_ENTITIES.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.STARTUP, Config.SPEC);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(MINER_ITEM);
        }
    }
}
