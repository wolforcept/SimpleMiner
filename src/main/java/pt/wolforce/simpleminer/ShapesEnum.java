package pt.wolforce.simpleminer;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public enum ShapesEnum {
    SPEED(new VoxelShape[]{
/*NORTH*/ Block.box(1, 0, 0, 15, 13, 6),
/*SOUTH*/ Block.box(1, 0, 10, 15, 13, 16),
/*EAST*/  Block.box(10, 0, 1, 16, 13, 15),
/*WEST*/  Block.box(0, 0, 1, 6, 13, 15),
    }),
    AREA(new VoxelShape[]{ //
/*NORTH*/ Shapes.or(Block.box(0, 0, 0, 16, 10, 4), Block.box(0, 0, 0, 16, 5, 10)),
/*SOUTH*/ Shapes.or(Block.box(0, 0, 12, 16, 10, 16), Block.box(0, 0, 6, 16, 5, 16)),
/*EAST*/  Shapes.or(Block.box(12, 0, 0, 16, 10, 16), Block.box(6, 0, 0, 16, 5, 16)),
/*WEST*/  Shapes.or(Block.box(0, 0, 0, 4, 10, 16), Block.box(0, 0, 0, 10, 5, 16)),
    }),
    EFFICIENCY(new VoxelShape[]{ //
/*NORTH*/ Shapes.or(Block.box(5, 0, 5, 11, 15, 11), Block.box(3, 0, 3, 13, 3, 13), Block.box(1, 1, 7, 9, 19, 9)),
/*SOUTH*/ Shapes.or(Block.box(5, 0, 5, 11, 15, 11), Block.box(3, 0, 3, 13, 3, 13), Block.box(7, 1, 7, 15, 19, 9)),
/*EAST*/  Shapes.or(Block.box(5, 0, 5, 11, 15, 11), Block.box(3, 0, 3, 13, 3, 13), Block.box(7, 1, 1, 9, 19, 9)),
/*WEST*/  Shapes.or(Block.box(5, 0, 5, 11, 15, 11), Block.box(3, 0, 3, 13, 3, 13), Block.box(7, 1, 7, 9, 19, 15)),
    });
    public final VoxelShape[] shape;

    ShapesEnum(VoxelShape[] shape) {
        this.shape = shape;
    }
}
