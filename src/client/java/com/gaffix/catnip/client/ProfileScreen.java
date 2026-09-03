package com.gaffix.catnip.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ProfileScreen extends Screen {
    private final Screen parent;

    public ProfileScreen(Screen parent) {
        super(Component.literal("Catnip Keybind Profiles"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int y = 35;
        for (KeybindProfile profile : ProfileManager.get().profiles) {
            addRenderableWidget(Button.builder(Component.literal("Use " + profile.name), button -> {
                ProfileManager.get().apply(profile, minecraft);
                minecraft.setScreenAndShow(null);
            }).bounds(width / 2 - 155, y, 205, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Edit"), button ->
                    minecraft.setScreenAndShow(new ProfileEditorScreen(this, profile)))
                    .bounds(width / 2 + 55, y, 100, 20).build());
            y += 24;
            if (y > height - 55) break;
        }

        addRenderableWidget(Button.builder(Component.literal("New profile"), button -> {
            KeybindProfile profile = new KeybindProfile("Profile " + ProfileManager.get().profiles.size());
            ProfileManager.get().profiles.add(profile);
            ProfileManager.get().save();
            minecraft.setScreenAndShow(new ProfileEditorScreen(this, profile));
        }).bounds(width / 2 - 155, height - 28, 150, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                .bounds(width / 2 + 5, height - 28, 150, 20).build());
    }

    @Override
    public void onClose() {
        minecraft.setScreenAndShow(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
