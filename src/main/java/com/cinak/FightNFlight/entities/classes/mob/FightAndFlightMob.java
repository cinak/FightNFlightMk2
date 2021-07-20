package com.cinak.FightNFlight.entities.classes.mob;

import com.cinak.FightNFlight.items.FightNFlightSpawnEgg;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public class FightAndFlightMob extends TameableEntity {
    protected FightAndFlightMob(EntityType<? extends TameableEntity> p_i48574_1_, World p_i48574_2_) {
        super(p_i48574_1_, p_i48574_2_);
    }

    private ActionResultType checkAndHandleImportantInteractions(PlayerEntity p_233661_1_, Hand p_233661_2_) {
        ItemStack itemstack = p_233661_1_.getItemInHand(p_233661_2_);
        if (itemstack.getItem() == Items.LEAD && this.canBeLeashed(p_233661_1_)) {
            this.setLeashedTo(p_233661_1_, true);
            itemstack.shrink(1);
            return ActionResultType.sidedSuccess(this.level.isClientSide);
        } else {
            if (itemstack.getItem() == Items.NAME_TAG) {
                ActionResultType actionresulttype = itemstack.interactLivingEntity(p_233661_1_, this, p_233661_2_);
                if (actionresulttype.consumesAction()) {
                    return actionresulttype;
                }
            }

            if (itemstack.getItem() instanceof FightNFlightSpawnEgg) {
                if (this.level instanceof ServerWorld) {
                    FightNFlightSpawnEgg spawneggitem = (FightNFlightSpawnEgg)itemstack.getItem();
                    Optional<MobEntity> optional = spawneggitem.spawnOffspringFromSpawnEgg(p_233661_1_, this, (EntityType)this.getType(), (ServerWorld)this.level, this.position(), itemstack);
                    optional.ifPresent((p_233658_2_) -> {
                        this.onOffspringSpawnedFromEgg(p_233661_1_, p_233658_2_);
                    });
                    return optional.isPresent() ? ActionResultType.SUCCESS : ActionResultType.PASS;
                } else {
                    return ActionResultType.CONSUME;
                }
            } else {
                return ActionResultType.PASS;
            }
        }
    }

    public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        return null;
    }
}
