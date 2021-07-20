package com.cinak.FightNFlight.Util;

import com.cinak.FightNFlight.FightNFlight;
import com.cinak.FightNFlight.entities.ModEntityTypes;
//import com.cinak.FightNFlight.entities.render.RajthorBreathRenderer;
import com.cinak.FightNFlight.entities.render.RajthorFireBallRenderer;
import com.cinak.FightNFlight.entities.render.RajthorRenderer;
import com.cinak.FightNFlight.items.FightNFlightSpawnEgg;
import net.minecraft.client.renderer.entity.DragonFireballRenderer;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = FightNFlight.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {

    @SubscribeEvent

    public static void onClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.RAJTHOR.get(), RajthorRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.RAJTHOR_FIREBALL.get(), RajthorFireBallRenderer::new);

        //RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.RAJTHOR_BREATH.get(), RajthorBreathRenderer::new);

    }
    @SubscribeEvent
    public static void onRegisterEntities(final RegistryEvent.Register<EntityType<?>> event) {
        FightNFlightSpawnEgg.initSpawnEggs();
    }

}
