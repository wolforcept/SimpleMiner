package pt.wolforce.simpleminer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.extensions.IForgeEntityMinecart;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Events {

    @SubscribeEvent
    public static void onCartTick(EntityEvent e) {
//        if (e != null && e.getEntity() != null && e.getEntity().level != null) {
//
//            Entity ent = e.getEntity();
//            World level = ent.level;
//            if (!(level instanceof ServerWorld)) return;
//            if (!(ent instanceof AbstractMinecartEntity)) return;
//            System.out.println(e.getEntity().getClass().getSimpleName());
//            IForgeEntityMinecart cart = (IForgeEntityMinecart) e.getEntity();
////            if (!(cart.level instanceof ServerWorld)) return;
////            FakePlayer fakePlayer = FakePlayerFactory.getMinecraft((ServerWorld) cart.level);
////            fakePlayer.setItemInHand(Hand.MAIN_HAND, new ItemStack(Items.COAL_BLOCK));
////            cart.interact(fakePlayer, Hand.MAIN_HAND);
//        }
    }
}
