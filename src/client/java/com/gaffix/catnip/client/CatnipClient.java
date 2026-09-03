package com.gaffix.catnip.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatnipClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("catnip");

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("catnip", "keybinds"));
        KeyMapping openProfiles = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.catnip.open_profiles", InputConstants.Type.KEYSYM,
                InputConstants.KEY_P, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openProfiles.consumeClick()) {
                client.setScreenAndShow(new ProfileScreen(null));
            }
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> ProfileManager.get().load(client));
    }
}
