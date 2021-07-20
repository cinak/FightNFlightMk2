package com.cinak.FightNFlight.entities.classes.projectile;


import com.cinak.FightNFlight.entities.ModEntityTypes;
import com.cinak.FightNFlight.entities.classes.mob.RajthorEntity;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.network.IPacket;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;


public class RajthorBreathEntity extends GudProjectileEntity {

    RajthorBreathEntity(EntityType<? extends GudProjectileEntity> p_i231584_1_, World p_i231584_2_) {
        super(p_i231584_1_, p_i231584_2_);
    }

    public boolean isPickable() {
        return false;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return null;
    }

    @Override
    protected void defineSynchedData() {

    }

    public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
        return false;
    }
}
