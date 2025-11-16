package pt.wolforce.simpleminer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinerBlock extends Block implements EntityBlock {
    public static final EnumProperty<MinerState> STATE = EnumProperty.create("state", MinerState.class);

    public MinerBlock(Properties properties) {
        super(properties.mapColor(MapColor.COLOR_MAGENTA).strength(2));
        this.registerDefaultState((this.stateDefinition.any().setValue(STATE, MinerState.OFF)));
    }

    private final VoxelShape shape = Shapes.or(
            Block.box(0, 0, 0, 16, 7, 16),
            Block.box(2, 7, 2, 14, 18, 14)
    );

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return shape;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return type == SimpleMiner.MINER_BLOCK_ENTITY.get() ? (l, pos, s, t) -> {
            if (l.getBlockEntity(pos) instanceof MinerBlockEntity minerBlockEntity) {
                minerBlockEntity.tick();
            }
        } : null;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return SimpleMiner.MINER_BLOCK_ENTITY.get().create(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(STATE);
    }
}
