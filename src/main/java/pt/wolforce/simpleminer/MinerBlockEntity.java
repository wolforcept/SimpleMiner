package pt.wolforce.simpleminer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinerBlockEntity extends BlockEntity {
    private static final int maxStuck = 100;
    private static ResourceHandler<ItemResource> crystalChest = null;

    private BlockPos target;
    private int time;
    private int stuck;

    public MinerBlockEntity(BlockPos pos, BlockState state) {
        super(SimpleMiner.MINER_BLOCK_ENTITY.get(), pos, state);
    }
    public void tick() {
        if (this.level == null || level.isClientSide()) return;

        var selfBlockState = this.level.getBlockState(this.getBlockPos());

        if (this.level.hasNeighborSignal(this.getBlockPos())) {
            SetMinerState(selfBlockState, MinerState.OFF);
            return;
        }

        if (stuck > 0) {
            stuck--;
            this.target = null;
            spawnParticlesFromChimney(ParticleTypes.SMOKE);
            SetMinerState(selfBlockState, MinerState.STUCK);
            return;
        }

        ItemStack crystal = findCrystal();
        if (crystal.isEmpty()) {
            this.stuck = maxStuck;
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
                breakBlock(target, crystal);
                time = 0;
                findTarget();
            } else {
                time++;
                spawnWorkingParticles();
            }
        } else {
            findTarget();
        }

        SetMinerState(selfBlockState, MinerState.ON);
    }

    private void SetMinerState(BlockState selfBlockState, MinerState minerState) {
        selfBlockState = selfBlockState.setValue(MinerBlock.STATE, minerState);
        this.level.setBlock(this.getBlockPos(), selfBlockState, Block.UPDATE_CLIENTS);
    }

    private void findTarget() {
        if (this.level == null) return;
        BlockPos currPos = getBlockPos();
        int nW = getNumberOfAreaUpgrades();
        int maxWidth = Config.BASE_AREA.get() + nW * Config.AREA_UPGRADE.get();
        int dy = 2;
        while (currPos.getY() - dy > 0) {
            for (int r = 0; r <= maxWidth; r++) {

                for (int dx = -r; dx <= r; dx++) {
                    for (int dz = -r; dz <= r; dz++) {

                        for (int i = -r; i < r; i++) {
                            BlockPos pos = currPos.offset(dx, -Math.max(2, dy - r), dz);
                            BlockState bs = this.level.getBlockState(pos);
                            if (!bs.isAir() && bs.getBlock() != Blocks.BEDROCK && bs.getFluidState().isEmpty()) {
                                target = pos;
                                return;
                            }
                        }

                    }
                }
            }
            dy++;
        }
        this.stuck = maxStuck;
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

    private List<ResourceHandler<ItemResource>> findChests() {
        List<ResourceHandler<ItemResource>> chests = new ArrayList<>();

        if (this.level == null) return chests;

        BlockPos thisPos = this.getBlockPos();
        BlockPos[] poss = new BlockPos[]{thisPos.west(),
                thisPos.east(),
                thisPos.north(),
                thisPos.south(),
                thisPos.west().north(),
                thisPos.east().north(),
                thisPos.west().south(),
                thisPos.east().south(),
        };

        for (BlockPos pos : poss) {
            BlockState bs = this.level.getBlockState(pos);
            if (bs.hasBlockEntity()) {
                BlockEntity blockEntity = this.level.getBlockEntity(pos);
                if (blockEntity == null) continue;
                var handler = level.getCapability(Capabilities.Item.BLOCK, pos, bs, blockEntity, null);
                if (handler !=  null) {
                    chests.add(handler);
                }
            }
        }

        return chests;
    }

    private void breakBlock(BlockPos target, ItemStack crystal) {
        if (this.level == null) return;

        List<ItemStack> drops = getDroppedStacks(target);
        try (var tx = Transaction.openRoot()) {
            for (ItemStack drop : drops) {
                var containers = findChests();
                if (containers.isEmpty()) {
                    this.stuck = maxStuck;
                    return;
                }

                var quantityToInsert = drop.getCount();
                for (ResourceHandler<ItemResource> container : containers) {
                    var inserted = container.insert(ItemResource.of(drop), quantityToInsert, tx);
                    quantityToInsert -= inserted;
                }

                if (quantityToInsert > 0) {
                    return;
                }
            }
            tx.commit();
        }


        if (crystal.isEmpty() || crystal.getDamageValue() >= crystal.getMaxDamage()) {
            this.stuck = maxStuck;
            return;
        }
        float eff = getNumberOfEfficiencyUpgrades() * Config.EFFICIENCY_CHANCE.get();
        if (Math.random() > eff) {
            damageCrystal(crystal);
        }
        this.level.removeBlock(target, false);
    }

    private void damageCrystal(ItemStack itemStack) {
        try (var tx = Transaction.openRoot()) {
            var crystal = ResourceHandlerUtil.findExtractableResource(
                    crystalChest,
                    itemResource -> itemResource.getItem() == itemStack.getItem()
                            && itemResource.toStack().getDamageValue() < itemResource.toStack().getMaxDamage(),
                    tx);

            if (crystal == null) {
                return;
            }

            int extracted = crystalChest.extract(crystal, 1, tx);
            if (extracted <= 0) {
                return ;
            }

            ItemStack stack = crystal.toStack();

            stack.setDamageValue(stack.getDamageValue() + 1);

            crystalChest.insert(ItemResource.of(stack), 1, tx);

            tx.commit();
        }

    }

    private ItemStack findCrystal() {
        var containers = findChests();
        if (containers.isEmpty()) {
            return ItemStack.EMPTY;
        }

        var crystal = ItemStack.EMPTY;
        try (var tx = Transaction.openRoot()) {
            for (var container : containers) {
                var crystalResource = ResourceHandlerUtil.findExtractableResource(container,
                        (itemResource) -> {
                            var itemStack = itemResource.toStack();
                            return itemResource.getItem() == SimpleMiner.CRYSTAL_ITEM.get() && itemStack.getDamageValue() < itemStack.getMaxDamage();
                        },
                        tx);
                if (crystalResource != null) {
                    crystal = crystalResource.toStack();
                    crystalChest = container;
                }
            }
            return crystal;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private List<ItemStack> getDroppedStacks(BlockPos blockPos) {
        ItemStack item = new ItemStack(Items.NETHERITE_PICKAXE);
        var enchant = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.FORTUNE).orElseGet(null);
        if (enchant != null) {
            item.enchant(enchant, 3);
        }
        return Block.getDrops(this.level.getBlockState(target), (ServerLevel) this.level, blockPos, this.level.getBlockEntity(blockPos), null, item);
    }

    private void spawnParticlesFromChimney(ParticleOptions type) {
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

    private void spawnParticles(ParticleOptions type, double x, double y, double z) {
        if (!(this.level instanceof ServerLevel serverWorld)) return;

        serverWorld.sendParticles(type, x, y, z, 1,
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
