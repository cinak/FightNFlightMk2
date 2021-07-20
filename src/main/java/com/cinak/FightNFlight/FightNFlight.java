package com.cinak.FightNFlight;

import com.cinak.FightNFlight.Util.RegistryHandler;
import com.cinak.FightNFlight.entities.ModEntityTypes;
import com.cinak.FightNFlight.entities.classes.mob.RajthorEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

// The fightnflight here should match an entry in the META-INF/mods.toml file
@Mod("fightnflight")
public class FightNFlight
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();


    public static final String MOD_ID = "fightnflight";

    public FightNFlight() {

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        //DispenserBlock.registerDispenseBehavior



        ModEntityTypes.ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());


        RegistryHandler.init();
        GeckoLib.initialize();


        MinecraftForge.EVENT_BUS.register(this);
    }


    private void setup(final FMLCommonSetupEvent event)
    {
        DeferredWorkQueue.runLater(() -> {
            GlobalEntityTypeAttributes.put(ModEntityTypes.RAJTHOR.get(), RajthorEntity.setCustomeAttributes().build());


        });
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);
    }

    public static final ItemGroup TAB = new ItemGroup("FlightNFlightItemTab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(RegistryHandler.ETERNAL_SNOW.get());
        }
    };
    public static ResourceLocation id(String path)
    {
        return new ResourceLocation(MOD_ID, path);
    }
}
