package net.hackerai.clickaura;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.hackerai.clickaura.config.ClickAuraConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClickAuraMod implements ClientModInitializer {
    public static final String MOD_ID = "clickaura";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ClickAuraModule MODULE;
    public static ClickAuraConfig CONFIG;
    public static ClickAuraHud HUD;

    @Override
    public void onInitializeClient() {
        CONFIG = new ClickAuraConfig();
        MODULE = new ClickAuraModule();
        HUD = new ClickAuraHud();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (MODULE.isEnabled() && client.player != null && client.world != null) {
                MODULE.tick(client);
            }
        });

        LOGGER.info("Click Aura initialized for 1.21.11");
    }
}
