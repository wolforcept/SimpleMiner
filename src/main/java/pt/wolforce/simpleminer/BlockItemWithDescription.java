package pt.wolforce.simpleminer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BlockItemWithDescription extends BlockItem  {

    private final String[] descr;

    public BlockItemWithDescription(Block block, Properties properties, String... descr) {
        super(block, properties);
        this.descr = descr;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> tooltipAdder, TooltipFlag flag) {
        if (flag.hasShiftDown()) {
            for (String str : descr)
                tooltipAdder.accept(Component.literal(str).withStyle(ChatFormatting.DARK_PURPLE));
        } else {
            tooltipAdder.accept(Component.literal("Press SHIFT to learn more.").withStyle(ChatFormatting.DARK_GRAY));
        }
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
    }
}
