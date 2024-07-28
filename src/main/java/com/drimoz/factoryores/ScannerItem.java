package com.drimoz.factoryores;


import com.drimoz.factoryores.core.domain.FO_Ore;
import com.drimoz.factoryores.core.domain.FO_OrePatch;
import com.drimoz.factoryores.core.infrastructure.FO_OreDataStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ColumnPos;
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
            FO_OrePatch playerOrePatch = FO_OreDataStorage.getInstance().getPlayerOrePatch(level, pos);

            if (playerOrePatch == null) {
                player.sendSystemMessage(Component.literal("No ore found at this location"));
            }
            else {
                FO_Ore positionOre = playerOrePatch.oresAtPosition(new ColumnPos(pos.getX(), pos.getY()));
                int positionOreCount = positionOre == null ? 0 : positionOre.currentCount();

                player.sendSystemMessage(
                        Component.literal(
                                "§5 Ore Patch : §7" + playerOrePatch.getPatchOre().toString() + "\n" +
                                        "§5 Base Count : §7" + playerOrePatch.getPatchBaseRichness() + "\n" +
                                        "§5 Remaining Ores : §7" + playerOrePatch.getPatchCurrentRichness() + "\n" +
                                        "============================" + "\n" +
                                        "§5 Position Remaining Ores : §7" + positionOreCount

                        )
                );

            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}
