package com.cinak.FightNFlight.entities;

import com.cinak.FightNFlight.FightNFlight;
import com.cinak.FightNFlight.entities.classes.mob.DrasterrEntity;
import com.cinak.FightNFlight.entities.classes.mob.RajthorEntity;
//import com.cinak.FightNFlight.entities.classes.projectile.RajthorBreathEntity;
import com.cinak.FightNFlight.entities.classes.projectile.RajthorBreathEntityer;
import com.cinak.FightNFlight.entities.classes.projectile.RajthorFireBallEntity;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntityTypes extends EntityType {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, FightNFlight.MOD_ID);


    public static final RegistryObject<EntityType<RajthorEntity>> RAJTHOR = ENTITY_TYPES.register("rajthor",
            () -> EntityType.Builder.of(RajthorEntity::new, EntityClassification.CREATURE)
                    .sized(2.4F,2.2F)
                    .clientTrackingRange(8)
                    .fireImmune()
                    .build(new ResourceLocation(FightNFlight.MOD_ID, "rajthor").toString())

    );

    public static final RegistryObject<EntityType<DrasterrEntity>> DRASTERR = ENTITY_TYPES.register("drasterr",
            () -> EntityType.Builder.of(DrasterrEntity::new, EntityClassification.WATER_CREATURE)
                    .sized(1.2F,1.2F)
                    .clientTrackingRange(8)
                    .build(new ResourceLocation(FightNFlight.MOD_ID, "drasterr").toString())

    );



    public static final RegistryObject<EntityType<RajthorFireBallEntity>> RAJTHOR_FIREBALL = ENTITY_TYPES.register("rajthor_fireball",
            () -> EntityType.Builder.<RajthorFireBallEntity>of(RajthorFireBallEntity::new, EntityClassification.MISC)
                    .sized(1.2F,1.2F)
                    .clientTrackingRange(8)
                    .build(new ResourceLocation(FightNFlight.MOD_ID, "rajthor_fireball").toString())

    );

   /* public static final RegistryObject<EntityType<FireballEntity>> RAJTHOR_BREATH = ENTITY_TYPES.register("rajthor_breath",
            () -> EntityType.Builder.of(RajthorBreathEntityer::new, EntityClassification.MISC)
                    .sized(1.2F,1.2F)
                    .clientTrackingRange(8)
                    .build(new ResourceLocation(FightNFlight.MOD_ID, "rajthor_breath").toString())

    );
    /*public static final RegistryObject<EntityType<RajthorBreathEntity>> RAJTHOR_BREATH = ENTITY_TYPES.register("rajthor_breath",
            () -> EntityType.Builder.of(RajthorBreathEntity::new, EntityClassification.MISC)
                    .sized(1.2F,1.2F)
                    .build(new ResourceLocation(FightNFlight.MOD_ID, "rajthor_breath").toString())

    );*/

    public ModEntityTypes(IFactory p_i231489_1_, EntityClassification p_i231489_2_, boolean p_i231489_3_, boolean p_i231489_4_, boolean p_i231489_5_, boolean p_i231489_6_, ImmutableSet p_i231489_7_, EntitySize p_i231489_8_, int p_i231489_9_, int p_i231489_10_) {
        super(p_i231489_1_, p_i231489_2_, p_i231489_3_, p_i231489_4_, p_i231489_5_, p_i231489_6_, p_i231489_7_, p_i231489_8_, p_i231489_9_, p_i231489_10_);
    }
}
