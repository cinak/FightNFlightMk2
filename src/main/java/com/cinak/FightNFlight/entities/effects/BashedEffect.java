package com.cinak.FightNFlight.entities.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.AttackDamageEffect;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;

public class BashedEffect extends Effect {
    public BashedEffect() {
        super(EffectType.HARMFUL, 13107200);
    }

    public void applyEffectTick(LivingEntity p_76394_1_, int p_76394_2_) {
        p_76394_1_.hurt(DamageSource.GENERIC.bypassArmor(), 1.5F);
        addAttributeModifier(Attributes.ATTACK_SPEED, "55FCED67-E92A-486E-9800-B47F202C4386", (double)-0.1F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", (double)-0.15F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        addAttributeModifier(Attributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0D, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public boolean isDurationEffectTick(int p_76397_1_, int p_76397_2_) {
        int i = 25 >> p_76397_2_;
        if (i > 0) {
            return p_76397_1_ % i == 0;
        } else {
            return true;
        }
    }
}
