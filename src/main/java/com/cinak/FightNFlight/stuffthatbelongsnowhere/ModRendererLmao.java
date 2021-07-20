package com.cinak.FightNFlight.stuffthatbelongsnowhere;

import com.cinak.FightNFlight.entities.ModEntityTypes;
import com.cinak.FightNFlight.entities.render.RajthorFireBallRenderer;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.DragonFireballRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.resources.IReloadableResourceManager;

//there was a stupid try
public class ModRendererLmao extends EntityRendererManager {

    public void registerRenderers(net.minecraft.client.renderer.ItemRenderer p_229097_1_, IReloadableResourceManager p_229097_2_) {
        this.register(ModEntityTypes.RAJTHOR_FIREBALL.get(), new RajthorFireBallRenderer(this));
    }
    public <T extends Entity> void register(EntityType<T> p_229087_1_, EntityRenderer<? super T> p_229087_2_) {
        this.renderers.put(p_229087_1_, p_229087_2_);
    }




    public ModRendererLmao(TextureManager p_i226034_1_, ItemRenderer p_i226034_2_, IReloadableResourceManager p_i226034_3_, FontRenderer p_i226034_4_, GameSettings p_i226034_5_) {
        super(p_i226034_1_, p_i226034_2_, p_i226034_3_, p_i226034_4_, p_i226034_5_);
    };


}
