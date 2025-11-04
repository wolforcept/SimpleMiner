package pt.wolforce.simpleminer;

import net.minecraft.item.Item;

public class ItemCrystal extends Item {

    public ItemCrystal() {
        super(new Item.Properties().tab(SimpleMiner.CREATIVE_TAB).durability(Config.CRYSTAL_SIZE.get()));
    }
}
