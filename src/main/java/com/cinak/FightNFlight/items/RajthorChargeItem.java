package com.cinak.FightNFlight.items;

import com.cinak.FightNFlight.FightNFlight;
import com.cinak.FightNFlight.Util.RegistryHandler;
import com.cinak.FightNFlight.entities.classes.mob.RajthorEntity;
import com.cinak.FightNFlight.entities.classes.projectile.RajthorFireBallEntity;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class RajthorChargeItem extends Item {
    public RajthorChargeItem() {

        super(new Properties().tab(FightNFlight.TAB));
        DispenserBlock.registerBehavior(this, RajthorChargeItem.DISPENSE_ITEM_BEHAVIOR);
    }


    private static final IDispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
        public ItemStack execute(IBlockSource p_82487_1_, ItemStack p_82487_2_) {

            Direction direction = p_82487_1_.getBlockState().getValue(DispenserBlock.FACING);
            IPosition iposition = DispenserBlock.getDispensePosition(p_82487_1_);
            double d0 = iposition.x() + (double)((float)direction.getStepX() * 0.3F);
            double d1 = iposition.y() + (double)((float)direction.getStepY() * 0.3F);
            double d2 = iposition.z() + (double)((float)direction.getStepZ() * 0.3F);
            World world = p_82487_1_.getLevel();
            Random random = world.random;
            double d3 = random.nextGaussian() * 0.05D + (double)direction.getStepX();
            double d4 = random.nextGaussian() * 0.05D + (double)direction.getStepY();
            double d5 = random.nextGaussian() * 0.05D + (double)direction.getStepZ();
            world.addFreshEntity(Util.make(new RajthorFireBallEntity(world, d0, d1, d2, d3, d4, d5), (p_229425_1_) -> {
                p_229425_1_.setItem(p_82487_2_);
            }));
            p_82487_2_.shrink(1);
            return p_82487_2_;
        }

        protected void playSound(IBlockSource p_82485_1_) {
            p_82485_1_.getLevel().levelEvent(1018, p_82485_1_.getPos(), 0);
        }
    };



    public ActionResultType useOn(ItemUseContext p_195939_1_) {
        World world = p_195939_1_.getLevel();
        BlockPos blockpos = p_195939_1_.getClickedPos();
        BlockState blockstate = world.getBlockState(blockpos);
        boolean flag = false;
        if (CampfireBlock.canLight(blockstate)) {
            this.playSound(world, blockpos);
            world.setBlockAndUpdate(blockpos, blockstate.setValue(CampfireBlock.LIT, Boolean.valueOf(true)));
            flag = true;
        } else {
            blockpos = blockpos.relative(p_195939_1_.getClickedFace());
            if (AbstractFireBlock.canBePlacedAt(world, blockpos, p_195939_1_.getHorizontalDirection())) {
                this.playSound(world, blockpos);
                world.setBlockAndUpdate(blockpos, AbstractFireBlock.getState(world, blockpos));
                flag = true;
            }
        }

        if (flag) {
            p_195939_1_.getItemInHand().shrink(1);
            return ActionResultType.sidedSuccess(world.isClientSide);
        } else {
            return ActionResultType.FAIL;
        }
    }

    private void playSound(World p_219995_1_, BlockPos p_219995_2_) {
        p_219995_1_.playSound((PlayerEntity)null, p_219995_2_, SoundEvents.FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
    }
}