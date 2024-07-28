package com.drimoz.factoryores.core.utils;

import com.drimoz.factoryores.FactoryOres;

public class FO_Loggers {

    public static void modLoadedInfo() {
        FactoryOres.LOGGER.info(FactoryOres.MOD_NAME + " - Loading Complete");
    }

    public static void eventCompletedInfo(String eventName) {
        FactoryOres.LOGGER.info(FactoryOres.MOD_NAME + " - Completed Event : {}", eventName);
    }
}
