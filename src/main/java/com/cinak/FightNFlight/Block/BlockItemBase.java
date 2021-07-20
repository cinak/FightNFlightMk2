package com.cinak.FightNFlight.Block;

import com.cinak.FightNFlight.FightNFlight;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class BlockItemBase extends BlockItem {
    public BlockItemBase(Block block) {
        super(block, new Properties().tab(FightNFlight.TAB));
    }
}
