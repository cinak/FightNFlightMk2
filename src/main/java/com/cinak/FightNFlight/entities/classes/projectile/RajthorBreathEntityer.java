package com.cinak.FightNFlight.entities.classes.projectile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RajthorBreathEntityer extends GudDamegeDewingProjectileEntity {
    private static final DataParameter<Boolean> DATA_DANGEROUS = EntityDataManager.defineId(RajthorBreathEntityer.class, DataSerializers.BOOLEAN);

    public RajthorBreathEntityer(EntityType<RajthorBreathEntityer> p_i501661, World p_i501662) {
        super(p_i501661, p_i501662);
    }

    protected float getInertia() {
        return this.isDangerous() ? 0.73F : super.getInertia();
    }

    public boolean isOnFire() {
        return false;
    }

    public float getBlockExplosionResistance(Explosion p_180428_1_, IBlockReader p_180428_2_, BlockPos p_180428_3_, BlockState p_180428_4_, FluidState p_180428_5_, float p_180428_6_) {
        return this.isDangerous() && p_180428_4_.canEntityDestroy(p_180428_2_, p_180428_3_, this) ? Math.min(0.8F, p_180428_6_) : p_180428_6_;
    }

    public void onEntityHits(Entity p_213868_1_) {
        super.onEntityHits(p_213868_1_);
        if (!this.level.isClientSide) {
            Entity entity = p_213868_1_.getEntity();
            Entity entity1 = this.getOwner();
            boolean flag;
            if (entity1 instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity)entity1;
                flag = entity.hurt(DamageSource.MAGIC, 8.0F);
                if (flag) {
                    if (entity.isAlive()) {
                        this.doEnchantDamageEffects(livingentity, entity);
                    } else {
                        livingentity.heal(5.0F);
                    }
                }
            } else {
                flag = entity.hurt(DamageSource.MAGIC, 5.0F);
            }

            if (flag && entity instanceof LivingEntity) {
                int i = 0;
                if (this.level.getDifficulty() == Difficulty.NORMAL) {
                    i = 10;
                } else if (this.level.getDifficulty() == Difficulty.HARD) {
                    i = 40;
                }

                if (i > 0) {
                    ((LivingEntity)entity).addEffect(new EffectInstance(Effects.WITHER, 20 * i, 1));
                }
            }

        }
    }

    public boolean isPickable() {
        return false;
    }

    public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
        return false;
    }

    protected void defineSynchedData() {
        this.entityData.define(DATA_DANGEROUS, true);
    }

    public boolean isDangerous() {
        return this.entityData.get(DATA_DANGEROUS);
    }

    public void setDangerous(boolean p_82343_1_) {
        this.entityData.set(DATA_DANGEROUS, p_82343_1_);
    }

    protected boolean shouldBurn() {
        return false;
    }
}
