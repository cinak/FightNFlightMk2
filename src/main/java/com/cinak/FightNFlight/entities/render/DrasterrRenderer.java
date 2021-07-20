package com.cinak.FightNFlight.entities.render;

import com.cinak.FightNFlight.entities.classes.mob.DrasterrEntity;
import com.cinak.FightNFlight.entities.classes.mob.RajthorEntity;
import com.cinak.FightNFlight.entities.client.DrasterrModel;
import com.cinak.FightNFlight.entities.client.RajthorModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class DrasterrRenderer extends GeoEntityRenderer<DrasterrEntity> {
    public DrasterrRenderer(EntityRendererManager renderManager) {
        super(renderManager, new DrasterrModel());
    }
    @Override
    public void renderEarly(DrasterrEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
                            float partialTicks) {
        super.renderEarly(entitylivingbaseIn, matrixStackIn, partialTickTime, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
        if(entitylivingbaseIn.isBaby()){
            matrixStackIn.scale(1F, 1F, 1F);
        }else{        matrixStackIn.scale(2F, 2F, 2F);
        }

    }


    @Override
    public RenderType getRenderType(DrasterrEntity animatable, float partialTicks, MatrixStack stack,
                                    IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        return RenderType.entitySolid(getTextureLocation(animatable));
    }
}
