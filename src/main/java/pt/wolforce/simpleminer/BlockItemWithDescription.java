package pt.wolforce.simpleminer;

import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class BlockItemWithDescription extends BlockItem {

    private final String[] descr;

    public BlockItemWithDescription(Block block, String... descr) {
        super(block, new Item.Properties().tab(SimpleMiner.CREATIVE_TAB));
        this.descr = descr;
    }

    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            for (String str : descr)
                tooltip.add(new StringTextComponent(str).withStyle(TextFormatting.DARK_PURPLE));
        } else {
            tooltip.add(new StringTextComponent("Press SHIFT to learn more.").withStyle(TextFormatting.DARK_GRAY));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
