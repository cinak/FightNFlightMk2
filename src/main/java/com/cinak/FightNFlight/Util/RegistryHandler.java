package com.cinak.FightNFlight.Util;

import com.cinak.FightNFlight.Block.BlockItemBase;
import com.cinak.FightNFlight.Block.RajthorEggBlock;
import com.cinak.FightNFlight.FightNFlight;
import com.cinak.FightNFlight.entities.ModEntityTypes;
import com.cinak.FightNFlight.items.FightNFlightSpawnEgg;
import com.cinak.FightNFlight.items.ItemBase;
import com.cinak.FightNFlight.items.RajthorChargeItem;
import com.cinak.FightNFlight.entities.effects.BashedEffect;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegistryHandler {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create (ForgeRegistries.ITEMS, FightNFlight.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create (ForgeRegistries.BLOCKS, FightNFlight.MOD_ID);
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create (ForgeRegistries.POTIONS, FightNFlight.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, FightNFlight.MOD_ID);
    public static void init() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        SOUND_EVENTS.register(FMLJavaModLoadingContext.get().getModEventBus());

    }
    public static final RegistryObject<Item> ETERNAL_SNOW = ITEMS.register("eternal_snow", ItemBase::new);
    public static final RegistryObject<Item> RAJTHOR_MAINSCALE = ITEMS.register("rajthor_mainscale", ItemBase::new);
    public static final RegistryObject<Item> RAJTHOR_CHARGE = ITEMS.register("rajthor_charge", RajthorChargeItem::new);
    public static final RegistryObject<FightNFlightSpawnEgg> RAJTHOR_SPAWN_EGG = ITEMS.register("rajthor_spawn_egg",
            () -> new FightNFlightSpawnEgg(ModEntityTypes.RAJTHOR, 0xA6F5F3 , 0x2B9A97, new Item.Properties().tab(FightNFlight.TAB)));


    public static final RegistryObject<SoundEvent> RAJTHOR_BREATH = register("entity.rajthor_entity.breath");
    public static final RegistryObject<SoundEvent> RAJTHOR_WALK = register("entity.rajthor_entity.walking");
    public static final RegistryObject<SoundEvent> RAJTHOR_HISS = register("entity.rajthor_entity.hisss");
    public static final RegistryObject<SoundEvent> RAJTHOR_HURT = register("entity.rajthor_entity.hurt");
    public static final RegistryObject<SoundEvent> RAJTHOR_DEATH = register("entity.rajthor_entity.death");


    public static final RegistryObject<Block> RAJTHOR_EGG_BLOCK = BLOCKS.register("rajthor_egg", RajthorEggBlock::new) ;
    public static final RegistryObject<Item> RAJTHOR_EGG_BLOCK_ITEM = ITEMS.register("rajthor_egg", ()-> new BlockItemBase(RAJTHOR_EGG_BLOCK.get()));

    public static RegistryObject<SoundEvent> register(String name)
    {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(FightNFlight.id(name)));
    }
    public static final RegistryObject<Effect> BASHED_EFFECT = EFFECTS.register("bashed_effect", BashedEffect:: new);

    public static RegistryObject<SoundEvent> RAJTHOR_BREATHS = SOUND_EVENTS.register("rajthor_breaths",
            () -> new SoundEvent(new ResourceLocation(FightNFlight.MOD_ID, "rajthor_breaths")));
}

