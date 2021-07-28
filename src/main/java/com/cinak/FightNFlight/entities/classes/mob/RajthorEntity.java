package com.cinak.FightNFlight.entities.classes.mob;

import com.cinak.FightNFlight.Block.RajthorEggBlock;
import com.cinak.FightNFlight.Util.RegistryHandler;
import com.cinak.FightNFlight.entities.ModEntityTypes;
import com.cinak.FightNFlight.entities.classes.projectile.RajthorBreathEntityer;
import com.cinak.FightNFlight.entities.classes.projectile.RajthorFireBallEntity;
import com.cinak.FightNFlight.entities.goals.FightNFlightSitGoal;
import com.cinak.FightNFlight.entities.goals.FightNflightFollowGoal;
import com.google.common.collect.Sets;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

public class RajthorEntity extends TameableEntity implements IAngerable , IAnimatable {
    private AnimationFactory factory = new AnimationFactory(this);
    private RajthorFireBallEntity rajthorfireEntity;

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if(event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.Rajthor.walk", true));
        }else if(this.isLayingEgg()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.Rajthor.dig", true));

        }

        else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.Rajthor.Idle", true));

        }return PlayState.CONTINUE;
    }
    private <E extends IAnimatable> PlayState predicateD(AnimationEvent<E> event) {
        if(this.isCharging()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.Rajthor.breath", true));

        }else if(this.isAttacking()){
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.Rajthor.bite", true));

        }

        else {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.Rajthor.Idle", true));

        }return PlayState.CONTINUE;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isCharging() {
        return this.entityData.get(DATA_IS_CHARGING);
    }

    public void setCharging(boolean p_175454_1_) {
        this.entityData.set(DATA_IS_CHARGING, p_175454_1_);
    }
    private static final DataParameter<Boolean> DATA_IS_CHARGING = EntityDataManager.defineId(RajthorEntity.class, DataSerializers.BOOLEAN);
    private int explosionPower = 1;
    public int getExplosionPower() {
        return this.explosionPower;
    }

    public RajthorEntity(EntityType<? extends TameableEntity> p_i48574_1_, World p_i48574_2_) {
        super(p_i48574_1_, p_i48574_2_);
        setTame(false);
        this.setPathfindingMalus(PathNodeType.WATER, 0.0F);
        this.moveControl = new RajthorEntity.MoveHelperController(this);
        this.setPathfindingMalus(PathNodeType.DANGER_FIRE, 0);
        this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, 0);
        this.setPathfindingMalus(PathNodeType.LAVA, 0.0F);
        this.maxUpStep = 1.0F;

    }


    public boolean isAttacking() {
        return this.entityData.get(DATA_ATTACKING_ID);
    }

    public void setAttacking(boolean p_189794_1_) {
        this.entityData.set(DATA_ATTACKING_ID, p_189794_1_);
    }
    private static final DataParameter<Boolean> DATA_ATTACKING_ID = EntityDataManager.defineId(RajthorEntity.class, DataSerializers.BOOLEAN);



    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
        data.addAnimationController(new AnimationController(this, "controllered", 0, this::predicateD));
    }

    class MeleeAttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {
        public MeleeAttackGoal() {
            super(RajthorEntity.this, 1.25D, true);
        }

        protected void checkAndPerformAttack(LivingEntity p_190102_1_, double p_190102_2_) {
            double d0 = this.getAttackReachSqr(p_190102_1_);
            if (p_190102_2_ <= d0 && this.isTimeToAttack()) {
                this.resetAttackCooldown();
                this.mob.doHurtTarget(p_190102_1_);
                RajthorEntity.this.setAttacking(false);
            } else if (p_190102_2_ <= d0 * 2.0D) {
                if (this.isTimeToAttack()) {
                    RajthorEntity.this.setAttacking(false);
                    this.resetAttackCooldown();
                }

                if (this.getTicksUntilNextAttack() <= 10) {
                    RajthorEntity.this.setAttacking(true);
                }
            } else {
                this.resetAttackCooldown();
                RajthorEntity.this.setAttacking(false);
            }

        }

        public void stop() {
            RajthorEntity.this.setAttacking(false);
            super.stop();
        }

        protected double getAttackReachSqr(LivingEntity p_179512_1_) {
            return (double)(4.0F + p_179512_1_.getBbWidth());
        }
    }
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ATTACKING_ID, false);
        this.entityData.define(DATA_IS_CHARGING, false);
        this.entityData.define(HAS_EGG, false);
        this.entityData.define(LAYING_EGG, false);
    }
    public void aiStep() {
        super.aiStep();
        if (this.isAlive() && this.isLayingEgg() && this.layEggCounter >= 1 && this.layEggCounter % 5 == 0) {
            BlockPos blockpos = this.blockPosition();
            if (RajthorEggBlock.onIce(this.level, blockpos)) {
                this.level.levelEvent(2001, blockpos, Block.getId(Blocks.BLUE_ICE.defaultBlockState()));
            }
        }

    }

    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (!this.isBaby() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.spawnAtLocation(Items.SCUTE, 1);
        }

    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public CreatureAttribute getMobType() {
        return CreatureAttribute.WATER;
    }


    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }




    static class MoveHelperController extends MovementController {
        private final RajthorEntity rajthorEntity;

        public MoveHelperController(RajthorEntity p_i48945_1_) {
            super(p_i48945_1_);
            this.rajthorEntity = p_i48945_1_;
        }

        public void tick() {
            if (this.rajthorEntity.isInWater()) {
                this.rajthorEntity.setDeltaMovement(this.rajthorEntity.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
            }

            if (this.operation == MovementController.Action.MOVE_TO && !this.rajthorEntity.getNavigation().isDone()) {
                double d0 = this.wantedX - this.rajthorEntity.getX();
                double d1 = this.wantedY - this.rajthorEntity.getY();
                double d2 = this.wantedZ - this.rajthorEntity.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (d3 < (double)2.5000003E-7F) {
                    this.mob.setZza(0.0F);
                } else {
                    float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                    this.rajthorEntity.yRot = this.rotlerp(this.rajthorEntity.yRot, f, 10.0F);
                    this.rajthorEntity.yBodyRot = this.rajthorEntity.yRot;
                    this.rajthorEntity.yHeadRot = this.rajthorEntity.yRot;
                    float f1 = (float)(this.speedModifier * this.rajthorEntity.getAttributeValue(Attributes.MOVEMENT_SPEED));
                    if (this.rajthorEntity.isInWater()) {
                        this.rajthorEntity.setSpeed(f1 * 0.02F);
                        float f2 = -((float)(MathHelper.atan2(d1, (double)MathHelper.sqrt(d0 * d0 + d2 * d2)) * (double)(180F / (float)Math.PI)));
                        f2 = MathHelper.clamp(MathHelper.wrapDegrees(f2), -85.0F, 85.0F);
                        this.rajthorEntity.xRot = this.rotlerp(this.rajthorEntity.xRot, f2, 5.0F);
                        float f3 = MathHelper.cos(this.rajthorEntity.xRot * ((float)Math.PI / 180F));
                        float f4 = MathHelper.sin(this.rajthorEntity.xRot * ((float)Math.PI / 180F));
                        this.rajthorEntity.zza = f3 * f1;
                        this.rajthorEntity.yya = -f4 * f1;
                    } else {
                        this.rajthorEntity.setSpeed(f1 * 0.1F);
                    }

                }
            } else {
                this.rajthorEntity.setSpeed(0.0F);
                this.rajthorEntity.setXxa(0.0F);
                this.rajthorEntity.setYya(0.0F);
                this.rajthorEntity.setZza(0.0F);
            }
        }
    }


    public void travel(Vector3d p_213352_1_) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.1F, p_213352_1_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(p_213352_1_);
        }

    }
    static class Navigator extends SwimmerPathNavigator {
        Navigator(RajthorEntity p_i48815_1_, World p_i48815_2_) {
            super(p_i48815_1_, p_i48815_2_);
        }

        protected boolean canUpdatePath() {
            return true;
        }

        protected PathFinder createPathFinder(int p_179679_1_) {
            this.nodeEvaluator = new WalkAndSwimNodeProcessor();
            return new PathFinder(this.nodeEvaluator, p_179679_1_);
        }

        public boolean isStableDestination(BlockPos p_188555_1_) {
            if (this.mob instanceof RajthorEntity) {
                RajthorEntity rajthorEntity = (RajthorEntity) this.mob;
            }


            return !this.level.getBlockState(p_188555_1_.below()).isAir();
        }
    }

    protected PathNavigator createNavigation(World p_175447_1_) {
        return new RajthorEntity.Navigator(this, p_175447_1_);
    }

    public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
        if (this.isInvulnerableTo(p_70097_1_)) {
            return false;
        } else {
            Entity entity = p_70097_1_.getEntity();
            this.setOrderedToSit(false);
            if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof AbstractArrowEntity)) {
                p_70097_2_ = (p_70097_2_ + 1.0F) / 2.0F;
            }

            return super.hurt(p_70097_1_, p_70097_2_);
        }
    }






    public static final Predicate<LivingEntity> PREY_SELECTOR = (p_213440_0_) -> {
        EntityType<?> entitytype = p_213440_0_.getType();
        return entitytype == EntityType.SHEEP || entitytype == EntityType.RABBIT || entitytype == EntityType.FOX || entitytype == EntityType.WOLF
                || entitytype == EntityType.COW || entitytype == EntityType.SALMON|| entitytype == EntityType.COD;
    };
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FightNFlightSitGoal(this));
        this.goalSelector.addGoal(1, new MateGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new RajthorEntity.MeleeAttackGoal());
        this.goalSelector.addGoal(3, new RajthorEntity.RajthorEntityEggLayGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new RajthorEntity.FireballAttackGoal(this, this.rajthorfireEntity));
        this.goalSelector.addGoal(5, new FightNflightFollowGoal(this,this, 4.0D, 30.0F, 2.0F, false));
        this.goalSelector.addGoal(6,  new RajthorEntity.Land(this, 3D, 10));
        this.goalSelector.addGoal(7, new RandomSwimmingGoal(this, 1.0D, 10));
        this.goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(9, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new RajthorEntity.HurtByTargetGoal());
        this.targetSelector.addGoal(4, new RajthorEntity.AttackPlayerGoal());
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(6, new NonTamedTargetGoal<>(this, AnimalEntity.class, false, PREY_SELECTOR));
        this.targetSelector.addGoal(7, new NonTamedTargetGoal<>(this, TurtleEntity.class, false, TurtleEntity.BABY_ON_LAND_SELECTOR));
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, FoxEntity.class, 10, true, true, (Predicate<LivingEntity>)null));
        this.targetSelector.addGoal(9, new ResetAngerGoal<>(this, true));
    }

    public boolean canFallInLove() {
        return super.canFallInLove() && !this.hasEgg();
    }

    class HurtByTargetGoal extends net.minecraft.entity.ai.goal.HurtByTargetGoal {
        public HurtByTargetGoal() {
            super(RajthorEntity.this);
        }

        public void start() {
            super.start();
            if (RajthorEntity.this.isBaby()) {
                this.alertOthers();
                this.stop();
            }

        }

        protected void alertOther(MobEntity p_220793_1_, LivingEntity p_220793_2_) {
            if (p_220793_1_ instanceof RajthorEntity && !p_220793_1_.isBaby()) {
                super.alertOther(p_220793_1_, p_220793_2_);
            }

        }
    }


    static class Land extends RandomWalkingGoal {
        private final RajthorEntity rajthorEntity;

        private Land(RajthorEntity rajthorEntity, double speedIn, int chance) {
            super(rajthorEntity, speedIn, chance);
            this.rajthorEntity = rajthorEntity;
        }

        public boolean canUse() {
            return !this.mob.isInWater() ? super.canUse() : false;
        }
    }


    public static AttributeModifierMap.MutableAttribute setCustomeAttributes() {
        return MobEntity.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 1.4D)
                .add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.ATTACK_DAMAGE, 14.0D)
                .add(Attributes.FOLLOW_RANGE, 100.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)

                .add(Attributes.ATTACK_KNOCKBACK, 1.5D);

    }



    public RajthorEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        RajthorEntity rajthorEntity = ModEntityTypes.RAJTHOR.get().create(p_241840_1_);
        UUID uuid = this.getOwnerUUID();
        if (uuid != null) {
            rajthorEntity.setOwnerUUID(uuid);
            rajthorEntity.setTame(true);
        }

        return rajthorEntity;
    }

    private BlockPos getTravelPos() {
        return this.entityData.get(TRAVEL_POS);
    }
    private static final DataParameter<BlockPos> TRAVEL_POS = EntityDataManager.defineId(RajthorEntity.class, DataSerializers.BLOCK_POS);


    public boolean canMate(AnimalEntity p_70878_1_) {
        if (p_70878_1_ == this) {
            return false;
        } else if (!this.isTame()) {
            return false;
        } else if (!(p_70878_1_ instanceof RajthorEntity)) {
            return false;
        } else {
            RajthorEntity rajthorEntity = (RajthorEntity) p_70878_1_;
            if (!rajthorEntity.isTame()) {
                return false;
            } else if (rajthorEntity.isInSittingPose()) {
                return false;
            } else {
                return this.isInLove() && rajthorEntity.isInLove();
            }
        }
    }
    public boolean wantsToAttack(LivingEntity p_142018_1_, LivingEntity p_142018_2_) {
        if (!(p_142018_1_ instanceof CreeperEntity) && !(p_142018_1_ instanceof GhastEntity)) {
            if (p_142018_1_ instanceof RajthorEntity) {
                RajthorEntity rajthorentity = (RajthorEntity) p_142018_1_;
                return !rajthorentity.isTame() || rajthorentity.getOwner() != p_142018_2_;
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
        this.setHasEgg(p_70037_1_.getBoolean("HasEgg"));
        if(!level.isClientSide) //FORGE: allow this entity to be read from nbt on client. (Fixes MC-189565)
            this.readPersistentAngerSaveData((ServerWorld)this.level, p_70037_1_);
        if (p_70037_1_.contains("ExplosionPower", 99)) {
            this.explosionPower = p_70037_1_.getInt("ExplosionPower");
        }

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
        p_213281_1_.putBoolean("HasEgg", this.hasEgg());
        this.addPersistentAngerSaveData(p_213281_1_);
        p_213281_1_.putInt("ExplosionPower", this.explosionPower);
    }
    public boolean isOnFire() {
        return false;
    }

    static class FireballAttackGoal extends Goal {
        private final RajthorEntity rajthorEntity;
        private final RajthorFireBallEntity rajthorfireEntity;
        public int chargeTime;
        private EntityType<RajthorFireBallEntity> rajthorfireEntity2;

        public FireballAttackGoal(RajthorEntity p_i45837_1_, RajthorFireBallEntity rajthorfireEntity) {
            this.rajthorEntity = p_i45837_1_;
            this.rajthorfireEntity = rajthorfireEntity;
        }


        public boolean canUse() {
            return this.rajthorEntity.getTarget() != null;
        }

        public void start() {
            this.chargeTime = 0;
        }

        public void stop() {
            this.rajthorEntity.setCharging(false);
        }

        public void tick() {
            LivingEntity livingentity = this.rajthorEntity.getTarget();
            double d0 = 64.0D;
            if (livingentity.distanceToSqr(this.rajthorEntity) < 4096.0D && livingentity.distanceToSqr(this.rajthorEntity) > 200.0D && this.rajthorEntity.canSee(livingentity)) {
                World world = this.rajthorEntity.level;
                ++this.chargeTime;
                if (this.chargeTime == 10 && !this.rajthorEntity.isSilent()) {
                    world.playLocalSound(rajthorEntity.getX() + 0.5D, rajthorEntity.getY() + 0.5D, rajthorEntity.getZ() + 0.5D, RegistryHandler.RAJTHOR_BREATH.get(), rajthorEntity.getSoundSource(), 1.0F + rajthorEntity.random.nextFloat(), rajthorEntity.random.nextFloat() * 0.7F + 0.3F, false);
                }

                if (this.chargeTime == 40) {
                    double d1 = 4.0D;
                    Vector3d vector3d = this.rajthorEntity.getViewVector(1.0F);
                    double d2 = livingentity.getX() - (this.rajthorEntity.getX() + vector3d.x * 4.0D);
                    double d3 = livingentity.getY(0.5D) - (0.5D + this.rajthorEntity.getY(0.5D));
                    double d4 = livingentity.getZ() - (this.rajthorEntity.getZ() + vector3d.z * 4.0D);
                    if (!this.rajthorEntity.isSilent()) {
                        world.playLocalSound(rajthorEntity.getX() + 0.5D, rajthorEntity.getY() + 0.5D, rajthorEntity.getZ() + 0.5D, RegistryHandler.RAJTHOR_BREATH.get(), rajthorEntity.getSoundSource(), 1.0F + rajthorEntity.random.nextFloat(), rajthorEntity.random.nextFloat() * 0.7F + 0.3F, false);
                    }

                    RajthorFireBallEntity fireballentity = new RajthorFireBallEntity(world, this.rajthorEntity, d2, d3, d4);
                    RajthorFireBallEntity fireballentity2 = new RajthorFireBallEntity(world, this.rajthorEntity, d2, d3, d4);

                    fireballentity.setPos(this.rajthorEntity.getX() + vector3d.x * 4.0D, this.rajthorEntity.getY(0.5D) + 0.5D, fireballentity.getZ() + vector3d.z * 4.0D);
                    fireballentity2.setPos(this.rajthorEntity.getX() + vector3d.x * 4.0D, this.rajthorEntity.getY(0.5D) + 0.5D, fireballentity.getZ() + vector3d.z * 4.0D);

                    world.addFreshEntity(fireballentity);
                    world.addFreshEntity(fireballentity2);
                    this.chargeTime = -60;
                }
            } else if (this.chargeTime > 0) {
                --this.chargeTime;
            }

            this.rajthorEntity.setCharging(this.chargeTime > 10);
        }
    }

    public float getWalkTargetValue(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
        if (p_205022_2_.getFluidState(p_205022_1_).is(FluidTags.WATER)) {
            return 10.0F;
        } else {
            return RajthorEggBlock.onIce(p_205022_2_, p_205022_1_) ? 10.0F : p_205022_2_.getBrightness(p_205022_1_) - 0.5F;
        }
    }
    public boolean hasEgg() {
        return this.entityData.get(HAS_EGG);
    }
    private static final DataParameter<Boolean> HAS_EGG = EntityDataManager.defineId(RajthorEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> LAYING_EGG = EntityDataManager.defineId(RajthorEntity.class, DataSerializers.BOOLEAN);
    private int layEggCounter;

    private void setHasEgg(boolean p_203017_1_) {
        this.entityData.set(HAS_EGG, p_203017_1_);
    }

    public boolean isLayingEgg() {
        return this.entityData.get(LAYING_EGG);
    }

    private void setLayingEgg(boolean p_203015_1_) {
        this.layEggCounter = p_203015_1_ ? 1 : 0;
        this.entityData.set(LAYING_EGG, p_203015_1_);
    }
    static class MateGoal extends BreedGoal {
        private final RajthorEntity rajthorEntity;

        MateGoal(RajthorEntity p_i48822_1_, double p_i48822_2_) {
            super(p_i48822_1_, p_i48822_2_);
            this.rajthorEntity = p_i48822_1_;
        }

        public boolean canUse() {
            return super.canUse() && !this.rajthorEntity.hasEgg();
        }

        protected void breed() {
            ServerPlayerEntity serverplayerentity = this.animal.getLoveCause();
            if (serverplayerentity == null && this.partner.getLoveCause() != null) {
                serverplayerentity = this.partner.getLoveCause();
            }

            if (serverplayerentity != null) {
                serverplayerentity.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.partner, (AgeableEntity)null);
            }

            this.rajthorEntity.setHasEgg(true);
            this.animal.resetLove();
            this.partner.resetLove();
            Random random = this.animal.getRandom();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level.addFreshEntity(new ExperienceOrbEntity(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
            }

        }
    }

    static class RajthorEntityEggLayGoal extends MoveToBlockGoal {
        private final RajthorEntity rajthorEntity;

        RajthorEntityEggLayGoal(RajthorEntity p_i48818_1_, double p_i48818_2_) {
            super(p_i48818_1_, p_i48818_2_, 16);
            this.rajthorEntity = p_i48818_1_;
        }

        public boolean canUse() {
            return this.rajthorEntity.hasEgg()  ? super.canUse() : false;
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.rajthorEntity.hasEgg() ;
        }

        public void tick() {
            super.tick();
            BlockPos blockpos = this.rajthorEntity.blockPosition();
            if (!this.rajthorEntity.isInWater() && this.isReachedTarget()) {
                if (this.rajthorEntity.layEggCounter < 1) {
                    this.rajthorEntity.setLayingEgg(true);
                } else if (this.rajthorEntity.layEggCounter > 200) {
                    World world = this.rajthorEntity.level;
                    world.playSound(null, blockpos, SoundEvents.TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
                    world.setBlock(this.blockPos.above(), RegistryHandler.RAJTHOR_EGG_BLOCK.get().defaultBlockState(), 3);
                    this.rajthorEntity.setHasEgg(false);
                    this.rajthorEntity.setLayingEgg(false);
                    this.rajthorEntity.setInLoveTime(600);
                }

                if (this.rajthorEntity.isLayingEgg()) {
                    this.rajthorEntity.layEggCounter++;
                }
            }

        }

        protected boolean isValidTarget(IWorldReader p_179488_1_, BlockPos p_179488_2_) {
            return !p_179488_1_.isEmptyBlock(p_179488_2_.above()) ? false : RajthorEggBlock.isIce(p_179488_1_, p_179488_2_);
        }
    }
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.randomValue(this.random));
    }
    class AttackPlayerGoal extends NearestAttackableTargetGoal<PlayerEntity> {
        public AttackPlayerGoal() {
            super(RajthorEntity.this, PlayerEntity.class, 20, true, true, (Predicate<LivingEntity>)null);
        }

        public boolean canUse() {
            if (RajthorEntity.this.isBaby()) {
                return false;
            } else {


                return false;
            }
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



    private static final RangedInteger PERSISTENT_ANGER_TIME = TickRangeConverter.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    private UUID persistentAngerTarget;


    public boolean doHurtTarget(Entity p_70652_1_) {
        boolean flag = p_70652_1_.hurt(DamageSource.sting(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
        if (flag) {
            this.doEnchantDamageEffects(this, p_70652_1_);
            if (p_70652_1_ instanceof LivingEntity) {
                ((LivingEntity)p_70652_1_).setStingerCount(((LivingEntity)p_70652_1_).getStingerCount() + 1);
                int i = 0;
                if (this.level.getDifficulty() == Difficulty.NORMAL) {
                    i = 10;
                } else if (this.level.getDifficulty() == Difficulty.HARD) {
                    i = 18;
                }

                if (i > 0) {
                    ((LivingEntity)p_70652_1_).addEffect(new EffectInstance(RegistryHandler.BASHED_EFFECT.get(), i * 20, 0));
                }
            }

            this.playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);
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
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(124.0D);
            this.setHealth(124.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8.0D);
        }

        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(18.0D);
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

}
