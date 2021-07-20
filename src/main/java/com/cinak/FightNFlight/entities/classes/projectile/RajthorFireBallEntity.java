package com.cinak.FightNFlight.entities.classes.projectile;

import com.cinak.FightNFlight.Util.RegistryHandler;
import com.cinak.FightNFlight.entities.ModEntityTypes;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;


public class RajthorFireBallEntity extends DamagingProjectileEntity {
    public int explosionPower = 1;


    public RajthorFireBallEntity(EntityType<RajthorFireBallEntity> p_i501661, World p_i501662) {
        super(p_i501661, p_i501662);
    }
    @OnlyIn(Dist.CLIENT)
    public RajthorFireBallEntity(World p_i1768_1_, double p_i1768_2_, double p_i1768_4_, double p_i1768_6_, double p_i1768_8_, double p_i1768_10_, double p_i1768_12_) {
        super(ModEntityTypes.RAJTHOR_FIREBALL.get(), p_i1768_2_, p_i1768_4_, p_i1768_6_, p_i1768_8_, p_i1768_10_, p_i1768_12_, p_i1768_1_);
    }

    public RajthorFireBallEntity(World p_i1769_1_, LivingEntity p_i1769_2_, double p_i1769_3_, double p_i1769_5_, double p_i1769_7_) {
        super(ModEntityTypes.RAJTHOR_FIREBALL.get(), p_i1769_2_, p_i1769_3_, p_i1769_5_, p_i1769_7_, p_i1769_1_);
    }
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


    protected void onHit(RayTraceResult p_70227_1_) {
        super.onHit(p_70227_1_);
        if (!this.level.isClientSide) {
            Explosion.Mode explosion$mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.getOwner()) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), 1.0F, false, explosion$mode);
            this.remove();
        }


        Entity entity = this.getOwner();
        if (p_70227_1_.getType() != RayTraceResult.Type.ENTITY || !((EntityRayTraceResult)p_70227_1_).getEntity().is(entity)) {
            if (!this.level.isClientSide) {
                List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D));
                AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(this.level, this.getX(), this.getY(), this.getZ());
                if (entity instanceof LivingEntity) {
                    areaeffectcloudentity.setOwner((LivingEntity)entity);
                }

                areaeffectcloudentity.setParticle(ParticleTypes.SOUL_FIRE_FLAME);
                areaeffectcloudentity.setRadius(0.5F);
                areaeffectcloudentity.setDuration(20);
                areaeffectcloudentity.setRadiusPerTick((7.0F - areaeffectcloudentity.getRadius()) / (float)areaeffectcloudentity.getDuration());
                areaeffectcloudentity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 1, 1));
                areaeffectcloudentity.addEffect(new EffectInstance(Effects.WEAKNESS, 1, 1));
                if (!list.isEmpty()) {
                    for(LivingEntity livingentity : list) {
                        double d0 = this.distanceToSqr(livingentity);
                        if (d0 < 16.0D) {
                            areaeffectcloudentity.setPos(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                            break;
                        }
                    }
                }

                this.level.levelEvent(2006, this.blockPosition(), this.isSilent() ? -1 : 1);
                this.level.addFreshEntity(areaeffectcloudentity);
                this.remove();
            }

        }

    }
    protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
        super.onHitEntity(p_213868_1_);
        if (!this.level.isClientSide) {
            Entity entity = p_213868_1_.getEntity();
            if (!entity.fireImmune()) {
                Entity entity1 = this.getOwner();
                boolean flag = entity.hurt(DamageSource.DRAGON_BREATH, 5.0F);
                if (!flag) {
                } else if (entity1 instanceof LivingEntity) {
                    this.doEnchantDamageEffects((LivingEntity)entity1, entity);
                }
            }

        }
    }

    protected void onHitBlock(BlockRayTraceResult p_230299_1_) {
        super.onHitBlock(p_230299_1_);
        if (!this.level.isClientSide) {
            Entity entity = this.getOwner();
            if (entity == null || !(entity instanceof MobEntity) || this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.getEntity())) {
                BlockPos blockpos = p_230299_1_.getBlockPos().relative(p_230299_1_.getDirection());
            }

        }
    }
    private static final DataParameter<ItemStack> DATA_ITEM_STACK = EntityDataManager.defineId(RajthorFireBallEntity.class, DataSerializers.ITEM_STACK);

    public void setItem(ItemStack p_213898_1_) {
        if (p_213898_1_.getItem() != RegistryHandler.RAJTHOR_CHARGE.get() || p_213898_1_.hasTag()) {
            this.getEntityData().set(DATA_ITEM_STACK, Util.make(p_213898_1_.copy(), (p_213897_0_) -> {
                p_213897_0_.setCount(1);
            }));
        }

    }

    protected IParticleData getTrailParticle() {
        return ParticleTypes.SOUL_FIRE_FLAME;
    }

    protected boolean shouldBurn() {
        return false;
    }
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
    }


    public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
        super.addAdditionalSaveData(p_213281_1_);
        p_213281_1_.putInt("ExplosionPower", this.explosionPower);

        ItemStack itemstack = this.getItemRaw();
        if (!itemstack.isEmpty()) {
            p_213281_1_.put("Item", itemstack.save(new CompoundNBT()));
        }
    }

    public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
        super.readAdditionalSaveData(p_70037_1_);
        if (p_70037_1_.contains("ExplosionPower", 99)) {
            this.explosionPower = p_70037_1_.getInt("ExplosionPower");
        }
        ItemStack itemstack = ItemStack.of(p_70037_1_.getCompound("Item"));
        this.setItem(itemstack);

    }


    protected ItemStack getItemRaw() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }


    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? new ItemStack(RegistryHandler.RAJTHOR_CHARGE.get()) : itemstack;
    }
}
