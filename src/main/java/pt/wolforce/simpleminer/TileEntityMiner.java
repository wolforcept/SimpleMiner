package pt.wolforce.simpleminer;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@SuppressWarnings({"deprecation"})
public class TileEntityMiner extends TileEntity implements ITickableTileEntity {

    private static final int maxStuck = 100;

    private BlockPos target;
    private int time;
    private int stuck;

    public TileEntityMiner() {
        super(SimpleMiner.MINER_TILE_ENTITY.get());
    }


    @Override
    public void tick() {

        if (this.level == null || this.level.isClientSide) return;

        if (this.level.hasNeighborSignal(this.getBlockPos())) return;

        if (stuck > 0) {
            stuck--;
            this.target = null;
            spawnParticlesFromChimney(ParticleTypes.SMOKE);
            return;
        }

        if (target != null) {
            BlockState bs = this.level.getBlockState(target);
            if (bs.isAir()) {
                target = null;
                stuck = maxStuck;
                return;
            }

            int speedMult = Config.BASE_SPEED.get() - (getNumberOfSpeedUpgrades() * Config.SPEED_UPGRADE.get());
            float maxTime = speedMult * bs.getDestroySpeed(this.level, target);
            float cutoff_time = Config.CUTOFF_TIME.get();
            float cutoff_value = Config.CUTOFF_VALUE.get();
            if (maxTime > cutoff_time) {
                maxTime -= cutoff_time;
                maxTime = cutoff_time + maxTime * cutoff_value;
            }

            if (time > maxTime) {
                breakBlock(target);
                time = 0;
                findTarget();
            } else {
                time++;
                spawnWorkingParticles();
            }
        } else {
            findTarget();
        }
    }

    private void findTarget() {

//        if (this.level == null) return;
//        BlockPos currPos = getBlockPos();
//        int nW = getNumberOfAreaUpgrades();
//        int maxWidth = Config.BASE_AREA.get() + nW * Config.AREA_UPGRADE.get();
//        int dy = 2;
//
//        while (currPos.getY() - dy > this.level.getWorldBorder().getMinZ()) {
//            // Calculate width for this layer (shrinks as you go down)
//            int layerWidth = Math.max(0, maxWidth - dy); // inverted pyramid shape
//
//            // Check center first
//            BlockPos centerPos = currPos.offset(0, -dy, 0);
//            if (checkBlock(centerPos)) return;
//
//            // Spiral outwards in square layers
//            for (int r = 1; r <= layerWidth; r++) {
//                // Top and bottom edges
//                for (int dx = -r; dx <= r; dx++) {
//                    if (checkBlock(currPos.offset(dx, -dy, -r)) || checkBlock(currPos.offset(dx, -dy, r))) return;
//                }
//                // Left and right edges (excluding corners)
//                for (int dz = -r + 1; dz <= r - 1; dz++) {
//                    if (checkBlock(currPos.offset(-r, -dy, dz)) || checkBlock(currPos.offset(r, -dy, dz))) return;
//                }
//            }
//            dy++;
//        }

//        if (this.level == null) return;
//        BlockPos currPos = getBlockPos();
//        int nW = getNumberOfAreaUpgrades();
//        int maxWidth = Config.BASE_AREA.get() + nW * Config.AREA_UPGRADE.get();
//        int dy = 2;
//        while (currPos.getY() - dy > this.level.getWorldBorder().getMinZ()) {
//
//            for (int side = 0; side < 4; side++) {
//                for (int r = 0; r < maxWidth; r++) {
//
//                    int dx = side % 2 == 0 ? r : 0;
//                    int dz = side % 2 != 0 ? r : 0;
//
//
//
//                }
//            }
//
//        }

        if (this.level == null) return;
        BlockPos currPos = getBlockPos();
        int nW = getNumberOfAreaUpgrades();
        int maxWidth = Config.BASE_AREA.get() + nW * Config.AREA_UPGRADE.get();
        int dy = 2;
        while (currPos.getY() - dy > 0) { //this.level.getWorldBorder().getMinZ()) {
            for (int r = 0; r <= maxWidth; r++) {

//                List<Function<Pair<Integer,Integer>, Vector2f>> sides = new LinkedList<>();
//                sides.add(p -> new Vector2f(p.getFirst(), p.getSecond()));
//                sides.add(p -> new Vector2f(p.getFirst(), p.getSecond()));
//                sides.add(p -> new Vector2f(p.getFirst(), p.getSecond()));
//                sides.add(p -> new Vector2f(p.getFirst(), p.getSecond()));

//                for (Function<Pair<Integer,Integer>, Vector2f> func : sides) {

                for (int dx = -r; dx <= r; dx++) {
                    for (int dz = -r; dz <= r; dz++) {

                        for (int i = -r; i < r; i++) {
                            BlockPos pos = currPos.offset(dx, -Math.max(2, dy - r), dz);
                            BlockState bs = this.level.getBlockState(pos);
                            if (!bs.isAir() && bs.getBlock() != Blocks.BEDROCK && !bs.getMaterial().isLiquid()) {
                                target = pos;
                                return;
                            }
                        }

                    }
                }
//                }
            }
            dy++;
        }
        this.stuck = maxStuck;
    }

    private boolean checkBlock(BlockPos pos) {
        if (this.level == null) return false;
        BlockState bs = this.level.getBlockState(pos);
        if (!bs.isAir() && bs.getBlock() != Blocks.BEDROCK) {
            target = pos;
            return true;
        }
        return false;
    }

    private int getNumberOfAreaUpgrades() {
        return getNumberOfUpgrades(SimpleMiner.AREA_BLOCK.get(), false);
    }

    private int getNumberOfSpeedUpgrades() {
        return getNumberOfUpgrades(SimpleMiner.SPEED_BLOCK.get(), false);
    }

    private int getNumberOfEfficiencyUpgrades() {
        return getNumberOfUpgrades(SimpleMiner.EFFICIENCY_BLOCK.get(), true);
    }

    private int getNumberOfUpgrades(Block block, boolean includeCorners) {
        if (this.level == null) return 0;
        BlockPos thisPos = this.getBlockPos();
        BlockPos[] poss = !includeCorners //
                ? new BlockPos[]{thisPos.west(), thisPos.east(), thisPos.north(), thisPos.south()} //
                : new BlockPos[]{thisPos.west(), thisPos.east(), thisPos.north(), thisPos.south(), //
                thisPos.west().south(), thisPos.east().north(), thisPos.west().north(), thisPos.east().south()};
        int n = 0;
        for (BlockPos pos : poss) {
            Block b = this.level.getBlockState(pos).getBlock();
            if (b == block) n++;
        }
        return n;
    }

    private Pair<IItemHandler, Integer> findChestFor(ItemStack stack) {
        IItemHandler itemHandler = findChest();

        if (itemHandler == null) return null;

        int slot = findSlotFor(stack, itemHandler);
        if (slot >= 0) {
            return new Pair<>(itemHandler, slot);
        }

        return null;
    }

    private IItemHandler findChest() {
        if (this.level == null) return null;

        BlockPos thisPos = this.getBlockPos();
        BlockPos[] poss = new BlockPos[]{thisPos.west(), //
                thisPos.east(), //
                thisPos.north(), //
                thisPos.south(), //
                thisPos.west().north(), //
                thisPos.east().north(), //
                thisPos.west().south(), //
                thisPos.east().south(), //
        };

        for (BlockPos pos : poss) {
            BlockState bs = this.level.getBlockState(pos);
            if (bs.hasTileEntity()) {
                TileEntity tile = this.level.getBlockEntity(pos);
                if (tile == null) continue;
                LazyOptional<IItemHandler> itemHandlerCap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (itemHandlerCap.isPresent()) {
                    //noinspection OptionalGetWithoutIsPresent
                    return itemHandlerCap.resolve().get();

                }
            }
        }

        return null;
    }

    private int findSlotFor(ItemStack stack, IItemHandler handler) {
        if (handler == null || stack.isEmpty()) return -1;

        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack remainder = handler.insertItem(slot, stack.copy(), true);
            if (remainder.getCount() < stack.getCount()) {
                return slot;
            }
        }

        return -1;
    }

    public static boolean areAllEmpty(List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void breakBlock(BlockPos target) {
        if (this.level == null) return;

        ItemStack crystal = findCrystal();

        if (crystal.isEmpty()) {
            this.stuck = maxStuck;
            return;
        }

        List<ItemStack> drops = getDroppedStacks(target);
        int tries = 0;
        while (!areAllEmpty(drops)) {

            tries++;
            if (tries > 1000) {
                this.stuck = maxStuck;
                return;
            }

            for (ItemStack drop : drops) {
                Pair<IItemHandler, Integer> pair = findChestFor(drop);
                if (pair == null) {
                    this.stuck = maxStuck;
                    return;
                }
                IItemHandler itemHandler = pair.getFirst();
                int slot = pair.getSecond();
                ItemStack leftovers = itemHandler.insertItem(slot, drop.copy(), false);
                drop.setCount(leftovers.getCount());
            }
        }


        if (crystal.isEmpty() || crystal.getDamageValue() >= crystal.getMaxDamage()) {
            this.stuck = maxStuck;
            return;
        }
        float eff = getNumberOfEfficiencyUpgrades() * Config.EFFICIENCY_CHANCE.get();
        if (Math.random() > eff) crystal.setDamageValue(crystal.getDamageValue() + 1);
        this.level.removeBlock(target, false);
    }

    private ItemStack findCrystal() {
        IItemHandler itemHandler = findChest();
        if (itemHandler == null) return ItemStack.EMPTY;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.getItem() == SimpleMiner.CRYSTAL_ITEM.get() && stack.getDamageValue() < stack.getMaxDamage())
                return stack;
        }
        return ItemStack.EMPTY;
    }

    @SuppressWarnings("ConstantConditions")
    private List<ItemStack> getDroppedStacks(BlockPos blockPos) {
        ItemStack item = new ItemStack(Items.NETHERITE_PICKAXE);
        item.enchant(Enchantments.BLOCK_FORTUNE, 3);
        return Block.getDrops(this.level.getBlockState(target), (ServerWorld) this.level, blockPos, this.level.getBlockEntity(blockPos), null, item);
    }

    private void spawnParticlesFromChimney(IParticleData type) {
        BlockPos pos = this.getBlockPos();
        if (Math.random() < .5) spawnParticles(type, pos.getX() + 0.33, pos.getY() + 1.55, pos.getZ() + 0.33);
    }

    private void spawnWorkingParticles() {
        if (Math.random() < .5) spawnParticlesFromChimney(ParticleTypes.CLOUD);
        List<Vector3d> points = getPointsOnLineToTarget();
        for (Vector3d p : points) {
            spawnParticles(ParticleTypes.FLAME, p.x, p.y, p.z);
        }
        spawnParticles(ParticleTypes.FLAME, target.getX() + .5, target.getY() + 1, target.getZ() + .5);
    }

    private void spawnParticles(IParticleData type, double x, double y, double z) {
        if (!(this.level instanceof ServerWorld)) return;

        ServerWorld serverWorld = (ServerWorld) this.level;
        serverWorld.sendParticles(type, x, y, z, 1, //
                0, 0, 0, 0);
    }

    private List<Vector3d> getPointsOnLineToTarget() {
        List<Vector3d> points = new ArrayList<>();
        BlockPos start = this.getBlockPos();
        int d = (int) (1 + Math.sqrt(start.distSqr(target)) / 5.0);

        for (int i = 0; i < d; i++) {
            double t = 1 - Math.min(1, Math.abs(new Random().nextGaussian() / 2));

            double x = start.getX() + 0.5 + (target.getX() - start.getX()) * t;
            double y = start.getY() + 0.5 + (target.getY() - start.getY()) * t;
            double z = start.getZ() + 0.5 + (target.getZ() - start.getZ()) * t;

            points.add(new Vector3d(x, y, z));

        }

        return points;
    }

}
