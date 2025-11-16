package pt.wolforce.simpleminer;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class Events {

    @SubscribeEvent
    public static void onCartTick(PlayerTickEvent.Post event) {
       /* if (Math.random() < 0.01F) {
            if (event.getEntity().level().isClientSide()) return;
            var item = event.getEntity().getItemHeldByArm(HumanoidArm.RIGHT);
             item.setDamageValue(510);
        }*/
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
