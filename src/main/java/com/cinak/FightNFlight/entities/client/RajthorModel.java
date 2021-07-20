package com.cinak.FightNFlight.entities.client;

import com.cinak.FightNFlight.FightNFlight;
import com.cinak.FightNFlight.entities.classes.mob.RajthorEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class RajthorModel extends AnimatedGeoModel<RajthorEntity> {

    public RajthorModel() {
    }
    @Override
    public ResourceLocation getAnimationFileLocation(RajthorEntity entity) {
        return new ResourceLocation(FightNFlight.MOD_ID, "animations/rajthor_entity.json");
    }

    @Override
    public ResourceLocation getModelLocation(RajthorEntity entity) {
        return new ResourceLocation(FightNFlight.MOD_ID, "geo/rajthors_better.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(RajthorEntity entity) {
        if(entity.isBaby()) {
            return new ResourceLocation(FightNFlight.MOD_ID, "textures/model/entity/rajthor_entitys_baby.png");
        }else
            return new ResourceLocation(FightNFlight.MOD_ID, "textures/model/entity/rajthor_entity_textures2.png");

    }

}
