package com.cinak.FightNFlight.entities.classes.mob;


import com.cinak.FightNFlight.Util.RegistryHandler;
import com.cinak.FightNFlight.entities.ModEntityTypes;
import com.cinak.FightNFlight.entities.classes.projectile.RajthorFireBallEntity;
import com.cinak.FightNFlight.entities.goals.FightNFlightSitGoal;
import com.cinak.FightNFlight.entities.goals.FightNflightFollowGoal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

public class DrasterrEntity extends TameableEntity implements IAngerable, IAnimatable {
    private RajthorEntity RajthorEntity;

    public DrasterrEntity(EntityType<? extends TameableEntity> p_i48574_1_, World p_i48574_2_) {
        super(p_i48574_1_, p_i48574_2_);
        setTame(false);
        this.setPathfindingMalus(PathNodeType.WATER, 0.0F);
        this.setPathfindingMalus(PathNodeType.DANGER_FIRE, 0);
        this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, 0);
        this.setPathfindingMalus(PathNodeType.LAVA, 0.0F);

    }
    private AnimationFactory factory = new AnimationFactory(this);

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if(event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.drasterr.swim", true));
        }else if(this.isAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.drasterr.chomp", true));

        }
        else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.drasterr.idle", true));

        }return PlayState.CONTINUE;
    }

    public void tick() {
        super.tick();
{
            int i = MathHelper.floor(this.getX());
            int j = MathHelper.floor(this.getY());
            int k = MathHelper.floor(this.getZ());
            if (this.isInWaterRainOrBubble() || !(this.level.getBiome(new BlockPos(i, 0, k)).getTemperature(new BlockPos(i, j, k)) > 1.0F)) {
                this.setMoisntessLevel(2400);
            } else {
                this.setMoisntessLevel(this.getMoistnessLevel() - 1);
                if (this.getMoistnessLevel() <= 0) {
                    this.hurt(DamageSource.DRY_OUT, 1.0F);
                }

                if (this.onGround) {
                    this.setDeltaMovement(this.getDeltaMovement().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5D, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F)));
                    this.yRot = this.random.nextFloat() * 360.0F;
                    this.onGround = false;
                    this.hasImpulse = true;
                }
            }

            if (this.level.isClientSide && this.isInWater() && this.getDeltaMovement().lengthSqr() > 0.03D) {
                Vector3d vector3d = this.getViewVector(0.0F);
                float f = MathHelper.cos(this.yRot * ((float)Math.PI / 180F)) * 0.3F;
                float f1 = MathHelper.sin(this.yRot * ((float)Math.PI / 180F)) * 0.3F;
                float f2 = 1.2F - this.random.nextFloat() * 0.7F;

                for(int v = 0; v < 2; ++v) {
                    this.level.addParticle(ParticleTypes.BUBBLE, this.getX() - vector3d.x * (double)f2 + (double)f, this.getY() - vector3d.y, this.getZ() - vector3d.z * (double)f2 + (double)f1, 0.0D, 0.0D, 0.0D);
                    this.level.addParticle(ParticleTypes.BUBBLE, this.getX() - vector3d.x * (double)f2 - (double)f, this.getY() - vector3d.y, this.getZ() - vector3d.z * (double)f2 - (double)f1, 0.0D, 0.0D, 0.0D);
                }
            }

        }
    }
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FightNFlightSitGoal(this));
        this.goalSelector.addGoal(0, new FindWaterGoal(this));
        this.goalSelector.addGoal(1, new DrasterrEntity.MeleeAttackGoal());
        this.goalSelector.addGoal(3, new FightNflightFollowGoal(this,RajthorEntity, 4.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(4, new DrasterrEntity.AttackPlayerGoal());
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(6, new NonTamedTargetGoal<>(this, AnimalEntity.class, false, PREY_SELECTOR));
        this.targetSelector.addGoal(7, new NonTamedTargetGoal<>(this, TurtleEntity.class, false, TurtleEntity.BABY_ON_LAND_SELECTOR));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, RajthorEntity.class, 10, true, true, (Predicate<LivingEntity>)null));
        this.targetSelector.addGoal(9, new ResetAngerGoal<>(this, true));
    }

    public static final Predicate<LivingEntity> PREY_SELECTOR = (p_213440_0_) -> {
        EntityType<?> entitytype = p_213440_0_.getType();
        return entitytype == EntityType.SHEEP || entitytype == EntityType.RABBIT || entitytype == EntityType.FOX || entitytype == EntityType.WOLF
                || entitytype == EntityType.COW || entitytype == EntityType.SALMON|| entitytype == EntityType.COD || entitytype == ModEntityTypes.RAJTHOR.get();
    };

    public DrasterrEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        DrasterrEntity drasterrEntity = ModEntityTypes.DRASTERR.get().create(p_241840_1_);
        UUID uuid = this.getOwnerUUID();
        if (uuid != null) {
            drasterrEntity.setOwnerUUID(uuid);
            drasterrEntity.setTame(true);
        }

        return drasterrEntity;
    }
    public boolean canMate(AnimalEntity p_70878_1_) {
        if (p_70878_1_ == this) {
            return false;
        } else if (!this.isTame()) {
            return false;
        } else if (!(p_70878_1_ instanceof DrasterrEntity)) {
            return false;
        } else {
            DrasterrEntity drasterrEntity = (DrasterrEntity) p_70878_1_;
            if (!drasterrEntity.isTame()) {
                return false;
            } else if (drasterrEntity.isInSittingPose()) {
                return false;
            } else {
                return this.isInLove() && drasterrEntity.isInLove();
            }
        }
    }
    public void travel(Vector3d p_213352_1_) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), p_213352_1_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(p_213352_1_);
        }

    }

    static class MoveHelperController extends MovementController {
        private final DolphinEntity dolphin;

        public MoveHelperController(DolphinEntity p_i48945_1_) {
            super(p_i48945_1_);
            this.dolphin = p_i48945_1_;
        }

        public void tick() {
            if (this.dolphin.isInWater()) {
                this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
            }

            if (this.operation == MovementController.Action.MOVE_TO && !this.dolphin.getNavigation().isDone()) {
                double d0 = this.wantedX - this.dolphin.getX();
                double d1 = this.wantedY - this.dolphin.getY();
                double d2 = this.wantedZ - this.dolphin.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (d3 < (double)2.5000003E-7F) {
                    this.mob.setZza(0.0F);
                } else {
                    float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                    this.dolphin.yRot = this.rotlerp(this.dolphin.yRot, f, 10.0F);
                    this.dolphin.yBodyRot = this.dolphin.yRot;
                    this.dolphin.yHeadRot = this.dolphin.yRot;
                    float f1 = (float)(this.speedModifier * this.dolphin.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    if (this.dolphin.isInWater()) {
                        this.dolphin.setSpeed(f1 * 0.02F);
                        float f2 = -((float)(MathHelper.atan2(d1, (double)MathHelper.sqrt(d0 * d0 + d2 * d2)) * (double)(180F / (float)Math.PI)));
                        f2 = MathHelper.clamp(MathHelper.wrapDegrees(f2), -85.0F, 85.0F);
                        this.dolphin.xRot = this.rotlerp(this.dolphin.xRot, f2, 5.0F);
                        float f3 = MathHelper.cos(this.dolphin.xRot * ((float)Math.PI / 180F));
                        float f4 = MathHelper.sin(this.dolphin.xRot * ((float)Math.PI / 180F));
                        this.dolphin.zza = f3 * f1;
                        this.dolphin.yya = -f4 * f1;
                    } else {
                        this.dolphin.setSpeed(f1 * 0.1F);
                    }

                }
            } else {
                this.dolphin.setSpeed(0.0F);
                this.dolphin.setXxa(0.0F);
                this.dolphin.setYya(0.0F);
                this.dolphin.setZza(0.0F);
            }
        }
    }
    public boolean wantsToAttack(LivingEntity p_142018_1_, LivingEntity p_142018_2_) {
        if (!(p_142018_1_ instanceof CreeperEntity) && !(p_142018_1_ instanceof GhastEntity)) {
            if (p_142018_1_ instanceof DrasterrEntity) {
                DrasterrEntity drasterrEntity = (DrasterrEntity) p_142018_1_;
                return !drasterrEntity.isTame() || drasterrEntity.getOwner() != p_142018_2_;
            } else if (p_142018_1_ instanceof PlayerEntity && p_142018_2_ instanceof PlayerEntity && !((PlayerEntity)p_142018_2_).canHarmPlayer((PlayerEntity)p_142018_1_)) {
                return false;
            } else if (p_142018_1_ instanceof AbstractHorseEntity && ((AbstractHorseEntity)p_142018_1_).isTamed()) {
                return false;
            } else {
                return !(p_142018_1_ instanceof TameableEntity) || !((TameableEntity)p_142018_1_).isTame();
            }
        } else {
            return false;
        }
    }


    public boolean canBeLeashed(PlayerEntity p_184652_1_) {
        return !this.isAngry() && super.canBeLeashed(p_184652_1_);
    }
    public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
        super.readAdditionalSaveData(p_70037_1_);
        this.setMoisntessLevel(p_70037_1_.getInt("Moistness"));
        if(!level.isClientSide) //FORGE: allow this entity to be read from nbt on client. (Fixes MC-189565)
            this.readPersistentAngerSaveData((ServerWorld)this.level, p_70037_1_);

    }

    protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
        this.playSound(RegistryHandler.RAJTHOR_WALK.get(), 1.0F, 1.0F);
    }
    protected float getSoundVolume() {
        return 5.0F;
    }


    protected SoundEvent getAmbientSound() {
        return RegistryHandler.RAJTHOR_HISS.get();
    }


    protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
        return RegistryHandler.RAJTHOR_HURT.get();

    }

    protected SoundEvent getDeathSound() {
        return RegistryHandler.RAJTHOR_DEATH.get();

    }

    public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
        super.addAdditionalSaveData(p_213281_1_);
        this.addPersistentAngerSaveData(p_213281_1_);
        p_213281_1_.putInt("Moistness", this.getMoistnessLevel());

    }

    public boolean isAttacking() {
        return this.entityData.get(DATA_ATTACKING_ID);
    }

    public void setAttacking(boolean p_189794_1_) {
        this.entityData.set(DATA_ATTACKING_ID, p_189794_1_);
    }
    private static final DataParameter<Boolean> DATA_ATTACKING_ID = EntityDataManager.defineId(DrasterrEntity.class, DataSerializers.BOOLEAN);

    class MeleeAttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {
        public MeleeAttackGoal() {
            super(DrasterrEntity.this, 1.25D, true);
        }

        protected void checkAndPerformAttack(LivingEntity p_190102_1_, double p_190102_2_) {
            double d0 = this.getAttackReachSqr(p_190102_1_);
            if (p_190102_2_ <= d0 && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(p_190102_1_);
                DrasterrEntity.this.setAttacking(false);
            } else if (p_190102_2_ <= d0 * 2.0D) {
                if (this.isTimeToAttack()) {
                    DrasterrEntity.this.setAttacking(false);
                    this.resetAttackCooldown();
                }

                if (this.getTicksUntilNextAttack() <= 10) {
                    DrasterrEntity.this.setAttacking(true);
                }
            } else {
                this.resetAttackCooldown();
                DrasterrEntity.this.setAttacking(false);
            }

        }

        public void stop() {
            DrasterrEntity.this.setAttacking(false);
            super.stop();
        }

        protected double getAttackReachSqr(LivingEntity p_179512_1_) {
            return (double)(4.0F + p_179512_1_.getBbWidth());
        }
    }
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACKING_ID, false);
        this.entityData.define(MOISTNESS_LEVEL, 2400);

    }


    public boolean canBreatheUnderwater() {
        return true;
    }

    public CreatureAttribute getMobType() {
        return CreatureAttribute.WATER;
    }



    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.randomValue(this.random));
    }
    class AttackPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {
        public AttackPlayerGoal() {
            super(DrasterrEntity.this, PlayerEntity.class, 20, true, true, (Predicate<LivingEntity>)null);
        }
        protected double getFollowDistance() {
            return super.getFollowDistance() * 0.5D;
        }
    }

    public void setRemainingPersistentAngerTime(int p_230260_1_) {
        this.remainingPersistentAngerTime = p_230260_1_;
    }

    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    public void setPersistentAngerTarget(@Nullable UUID p_230259_1_) {
        this.persistentAngerTarget = p_230259_1_;
    }

    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }



    private static final DataParameter<Integer> DATA_REMAINING_ANGER_TIME = EntityDataManager.defineId(DrasterrEntity.class, DataSerializers.INT);



    private static final RangedInteger PERSISTENT_ANGER_TIME = TickRangeConverter.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;


    public boolean doHurtTarget(Entity p_70652_1_) {
        boolean flag = p_70652_1_.hurt(DamageSource.mobAttack(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
        if (flag) {
            this.doEnchantDamageEffects(this, p_70652_1_);
        }

        return flag;
    }

    public boolean isFood(ItemStack p_70877_1_) {
        Item item = p_70877_1_.getItem();
        return item.isEdible() && item.getFoodProperties().isMeat();
    }



    public void setTame(boolean p_70903_1_) {
        super.setTame(p_70903_1_);
        if (p_70903_1_) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0D);
            this.setHealth(20.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8.0D);
        }

        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    public ActionResultType mobInteract(PlayerEntity p_230254_1_, Hand p_230254_2_) {
        ItemStack itemstack = p_230254_1_.getItemInHand(p_230254_2_);
        Item item = itemstack.getItem();
        if (this.level.isClientSide) {
            boolean flag = this.isOwnedBy(p_230254_1_) || this.isTame() || item == RegistryHandler.ETERNAL_SNOW.get() && !this.isTame() && !this.isAngry();
            return flag ? ActionResultType.CONSUME : ActionResultType.PASS;
        } else {
            if (this.isTame()) {
                if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    if (!p_230254_1_.abilities.instabuild) {
                        itemstack.shrink(1);
                    }

                    this.heal((float)item.getFoodProperties().getNutrition());
                    return ActionResultType.SUCCESS;
                }
                ActionResultType actionresulttype = super.mobInteract(p_230254_1_, p_230254_2_);
                if ((!actionresulttype.consumesAction() || this.isBaby()) && this.isOwnedBy(p_230254_1_)) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget((LivingEntity)null);
                    return ActionResultType.SUCCESS;
                }
            } else if (item == RegistryHandler.ETERNAL_SNOW.get() && !this.isAngry()) {
                if (!p_230254_1_.abilities.instabuild) {
                    itemstack.shrink(1);
                }

                if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, p_230254_1_)) {
                    this.tame(p_230254_1_);
                    UUID uuid = this.getOwnerUUID();
                    this.setOwnerUUID(uuid);
                    this.setTame(true);
                    this.navigation.stop();
                    this.setTarget((LivingEntity)null);

                    this.level.broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte)6);
                }

                return ActionResultType.SUCCESS;
            }

            return super.mobInteract(p_230254_1_, p_230254_2_);
        }
    }
    private static final DataParameter<Integer> MOISTNESS_LEVEL = EntityDataManager.defineId(DrasterrEntity.class, DataSerializers.INT);
    public int getMoistnessLevel() {
        return this.entityData.get(MOISTNESS_LEVEL);
    }

    public void setMoisntessLevel(int p_211137_1_) {
        this.entityData.set(MOISTNESS_LEVEL, p_211137_1_);
    }

}
