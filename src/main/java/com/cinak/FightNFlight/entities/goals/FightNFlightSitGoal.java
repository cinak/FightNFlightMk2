package com.cinak.FightNFlight.entities.goals;

import java.util.EnumSet;

import com.cinak.FightNFlight.entities.classes.mob.FightAndFlightMob;
import com.cinak.FightNFlight.entities.classes.mob.RajthorEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.TameableEntity;


public class FightNFlightSitGoal extends Goal {
    private final TameableEntity mob;

    public FightNFlightSitGoal(TameableEntity p_i1654_1_) {
        this.mob = p_i1654_1_;

        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    public boolean canContinueToUse() {
        return this.mob.isOrderedToSit();
    }

    public boolean canUse() {
        if (!this.mob.isTame()) {
            return false;
        } else if (!this.mob.isOnGround() && !this.mob.isInWater()) {
            return false;
        } else {
            LivingEntity livingentity = this.mob.getOwner();
            if (livingentity == null) {
                return true;
            } else {
                return this.mob.distanceToSqr(livingentity) < 144.0D && livingentity.getLastHurtByMob() != null ? false : this.mob.isOrderedToSit();
            }
        }
    }


    public void start() {
        this.mob.getNavigation().stop();
        this.mob.setInSittingPose(true);
    }

    public void stop() {
        this.mob.setInSittingPose(false);
    }

}