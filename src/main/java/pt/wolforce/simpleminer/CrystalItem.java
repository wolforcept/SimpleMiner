package pt.wolforce.simpleminer;


import net.minecraft.world.item.Item;

public class CrystalItem extends Item {

    public CrystalItem(Properties properties) {
        super(properties.durability(Config.CRYSTAL_SIZE.get()));
    }
}
