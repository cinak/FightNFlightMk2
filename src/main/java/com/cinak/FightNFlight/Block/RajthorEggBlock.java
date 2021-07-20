package com.cinak.FightNFlight.Block;

import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;

import com.cinak.FightNFlight.Util.RegistryHandler;
import com.cinak.FightNFlight.entities.ModEntityTypes;
import com.cinak.FightNFlight.entities.classes.mob.RajthorEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

public class RajthorEggBlock extends Block {
    public static final IntegerProperty HATCH = BlockStateProperties.HATCH;



    public RajthorEggBlock() {
        super(AbstractBlock.Properties.of(Material.METAL)
                .strength(5.0f, 6.0f)
                .sound(SoundType.BONE_BLOCK)
                .harvestLevel(3)
                .harvestTool(ToolType.PICKAXE)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(HATCH, Integer.valueOf(0)));

    }


    public void randomTick(BlockState p_225542_1_, ServerWorld p_225542_2_, BlockPos p_225542_3_, Random p_225542_4_) {
        if (this.shouldUpdateHatchLevel(p_225542_2_) && onIce(p_225542_2_, p_225542_3_)) {
            int i = p_225542_1_.getValue(HATCH);
            if (i < 2) {
                BlockPos blockpos = p_225542_3_.offset(p_225542_2_.random.nextInt(16) - p_225542_2_.random.nextInt(16), p_225542_2_.random.nextInt(8) - p_225542_2_.random.nextInt(8), p_225542_2_.random.nextInt(16) - p_225542_2_.random.nextInt(16));

                p_225542_2_.playSound((PlayerEntity)null, p_225542_3_, RegistryHandler.RAJTHOR_HURT.get(), SoundCategory.BLOCKS, 0.7F, 0.9F + p_225542_4_.nextFloat() * 0.2F);
                double d0 = p_225542_2_.random.nextDouble();
                double d1 = MathHelper.lerp(d0, (double)blockpos.getX(), (double)p_225542_3_.getX()) + (p_225542_2_.random.nextDouble() - 0.5D) + 0.5D;
                double d2 = MathHelper.lerp(d0, (double)blockpos.getY(), (double)p_225542_3_.getY()) + p_225542_2_.random.nextDouble() - 0.5D;
                double d3 = MathHelper.lerp(d0, (double)blockpos.getZ(), (double)p_225542_3_.getZ()) + (p_225542_2_.random.nextDouble() - 0.5D) + 0.5D;
                float f = (p_225542_2_.random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (p_225542_2_.random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (p_225542_2_.random.nextFloat() - 0.5F) * 0.2F;
                p_225542_2_.addParticle(ParticleTypes.PORTAL, d1, d2, d3, (double)f, (double)f1, (double)f2);
                p_225542_2_.setBlock(p_225542_3_, p_225542_1_.setValue(HATCH, Integer.valueOf(i + 1)), 2);
            } else {
                p_225542_2_.playSound((PlayerEntity)null, p_225542_3_, RegistryHandler.RAJTHOR_HURT.get(), SoundCategory.BLOCKS, 0.7F, 0.9F + p_225542_4_.nextFloat() * 0.2F);
                p_225542_2_.removeBlock(p_225542_3_, false);

                    p_225542_2_.levelEvent(2001, p_225542_3_, Block.getId(p_225542_1_));
                    RajthorEntity rajthorEntity = ModEntityTypes.RAJTHOR.get().create(p_225542_2_);
                    rajthorEntity.setAge(-24000);
                    p_225542_2_.addFreshEntity(rajthorEntity);

            }
        }

    }
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPE;
    }

    public static final VoxelShape SHAPE = Stream.of(
            Block.box(1, 4, 1, 15, 11, 15),
            Block.box(2, 3, 2, 14, 6, 14),
            Block.box(2, 0, 2, 14, 5, 14),
            Block.box(2, 9, 2, 14, 12, 14),
            Block.box(2, 10, 2, 14, 15, 14),
            Block.box(2, 11, 14, 14, 13, 15),
            Block.box(2, 2, 6, 14, 4, 15),
            Block.box(2, 11, 1, 14, 13, 2),
            Block.box(2, 2, 1, 14, 4, 10),
            Block.box(1, 11, 2, 2, 13, 14),
            Block.box(1, 2, 2, 10, 4, 14),
            Block.box(14, 11, 2, 15, 13, 14),
            Block.box(6, 2, 2, 15, 4, 14)
    ).reduce((v1, v2) -> {return VoxelShapes.join(v1, v2, IBooleanFunction.OR);}).get();



    public static boolean onIce(IBlockReader p_203168_0_, BlockPos p_203168_1_) {
        return isIce(p_203168_0_, p_203168_1_.below());
    }

    public static boolean isIce(IBlockReader p_241473_0_, BlockPos p_241473_1_) {
        return p_241473_0_.getBlockState(p_241473_1_).is(BlockTags.ICE);
    }

    public void onPlace(BlockState p_220082_1_, World p_220082_2_, BlockPos p_220082_3_, BlockState p_220082_4_, boolean p_220082_5_) {
        if (onIce(p_220082_2_, p_220082_3_) && !p_220082_2_.isClientSide) {
            p_220082_2_.levelEvent(2005, p_220082_3_, 0);
        }

    }

    private boolean shouldUpdateHatchLevel(World p_203169_1_) {
        float f = p_203169_1_.getTimeOfDay(1.0F);
        if ((double)f < 0.69D && (double)f > 0.65D) {
            return true;
        } else {
            return p_203169_1_.random.nextInt(500) == 0;
        }
    }

    public void playerDestroy(World p_180657_1_, PlayerEntity p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_, @Nullable TileEntity p_180657_5_, ItemStack p_180657_6_) {
        super.playerDestroy(p_180657_1_, p_180657_2_, p_180657_3_, p_180657_4_, p_180657_5_, p_180657_6_);
    }



    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(HATCH);
    }


}
