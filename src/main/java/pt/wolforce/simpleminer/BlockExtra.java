package pt.wolforce.simpleminer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

public class BlockExtra extends Block {

    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    private final int shapeIndex;

    public BlockExtra(int shapeIndex) {
        super(Properties.of(Material.STONE).strength(2).harvestTool(ToolType.PICKAXE));
        this.shapeIndex = shapeIndex;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    private static final VoxelShape[][] shapes = new VoxelShape[][]{ //
            new VoxelShape[]{ //
/*NORTH*/ Block.box(1, 0, 0, 15, 13, 6),
/*SOUTH*/ Block.box(1, 0, 10, 15, 13, 16),
/*EAST*/  Block.box(10, 0, 1, 16, 13, 15),
/*WEST*/  Block.box(0, 0, 1, 6, 13, 15), //
            }, //
            new VoxelShape[]{ //
/*NORTH*/ VoxelShapes.or(Block.box(0, 0, 0, 16, 10, 4), Block.box(0, 0, 0, 16, 5, 10)),
/*SOUTH*/ VoxelShapes.or(Block.box(0, 0, 12, 16, 10, 16), Block.box(0, 0, 6, 16, 5, 16)),
/*EAST*/  VoxelShapes.or(Block.box(12, 0, 0, 16, 10, 16), Block.box(6, 0, 0, 16, 5, 16)),
/*WEST*/  VoxelShapes.or(Block.box(0, 0, 0, 4, 10, 16), Block.box(0, 0, 0, 10, 5, 16)), //
            }, //
            new VoxelShape[]{ //
/*NORTH*/ VoxelShapes.or(Block.box(5, 0, 5, 11, 15, 11), Block.box(3, 0, 3, 13, 3, 13), Block.box(1, 1, 7, 9, 19, 9)),
/*SOUTH*/ VoxelShapes.or(Block.box(5, 0, 5, 11, 15, 11), Block.box(3, 0, 3, 13, 3, 13), Block.box(7, 1, 7, 15, 19, 9)),
/*EAST*/  VoxelShapes.or(Block.box(5, 0, 5, 11, 15, 11), Block.box(3, 0, 3, 13, 3, 13), Block.box(7, 1, 1, 9, 19, 9)),
/*WEST*/  VoxelShapes.or(Block.box(5, 0, 5, 11, 15, 11), Block.box(3, 0, 3, 13, 3, 13), Block.box(7, 1, 7, 9, 19, 15)), //
            }, //
    };

    @Override
    public VoxelShape getShape(BlockState bs, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        switch (bs.getValue(FACING)) {
            case SOUTH:
                return shapes[shapeIndex][1];
            case EAST:
                return shapes[shapeIndex][2];
            case WEST:
                return shapes[shapeIndex][3];
            case NORTH:
            default:
                return shapes[shapeIndex][0];
        }
    }

}
