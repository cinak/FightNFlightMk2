package com.cinak.FightNFlight.entities.client;

import com.cinak.FightNFlight.FightNFlight;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DrasterrModel extends AnimatedGeoModel {
    @Override
    public ResourceLocation getAnimationFileLocation(Object entity) {
        return new ResourceLocation(FightNFlight.MOD_ID, "animations/drasterr_entity.json");
    }

    @Override
    public ResourceLocation getModelLocation(Object entity) {
        return new ResourceLocation(FightNFlight.MOD_ID, "geo/drasterr_entity.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(Object entity) {
        return new ResourceLocation(FightNFlight.MOD_ID, "textures/model/entity/drasterr.png");
    }

}
