package com.drimoz.factoryores;


import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ScannerItem extends Item {

    public ScannerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();

        if (!level.isClientSide && player != null) {
            FakeOrePatchGenerator.FakeOreData data = ((ServerLevel) level).getDataStorage().computeIfAbsent(
                    FakeOrePatchGenerator.FakeOreData::load,
                    FakeOrePatchGenerator.FakeOreData::new,
                    FakeOrePatchGenerator.FakeOreData.DATA_NAME
            );

            if (data != null) {
                FakeOrePatchGenerator.FakeOreData.OrePatch patch = data.getPatchAt(pos);
                if (patch != null) {
                    player.sendSystemMessage(Component.literal("Ore found! Initial amount: " + patch.getInitialCount() + ", Current amount: " + patch.getCurrentCount()));
                } else {
                    player.sendSystemMessage(Component.literal("No ore found at this location"));
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}
