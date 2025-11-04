package pt.wolforce.simpleminer;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

public class BlockMiner extends Block {

    public BlockMiner() {
        super(AbstractBlock.Properties.of(Material.STONE).strength(2).harvestTool(ToolType.PICKAXE));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return SimpleMiner.MINER_TILE_ENTITY.get().create();
    }

    private VoxelShape shape = VoxelShapes.or(
            Block.box(0, 0, 0, 16, 7, 16),
            Block.box(2, 7, 2, 14, 18, 14)
    );

    @Override
    public VoxelShape getShape(BlockState bs, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return shape;
    }
}
