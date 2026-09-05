package com.gaffix.catnip.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;

public final class BindingPickerScreen extends Screen {
    private static final int PAGE_SIZE = 8;
    private final Screen parent;
    private final KeybindProfile profile;
    private int page;

    public BindingPickerScreen(Screen parent, KeybindProfile profile) {
        super(Component.literal("Choose a control"));
        this.parent = parent;
        this.profile = profile;
    }

    @Override
    protected void init() {
        List<KeyMapping> available = Arrays.stream(minecraft.options.keyMappings)
                .filter(CatnipClient::canBeProfiled)
                .filter(mapping -> !profile.bindings.containsKey(mapping.getName())).toList();
        int start = page * PAGE_SIZE;
        int y = 30;
        for (int i = start; i < Math.min(start + PAGE_SIZE, available.size()); i++) {
            KeyMapping mapping = available.get(i);
            addRenderableWidget(Button.builder(Component.translatable(mapping.getName()), button -> {
                profile.bindings.put(mapping.getName(), mapping.saveString());
                ProfileManager.get().save();
                minecraft.setScreenAndShow(parent);
            }).bounds(width / 2 - 150, y, 300, 20).build());
            y += 24;
        }
        if (page > 0) addRenderableWidget(Button.builder(Component.literal("< Previous"), button -> { page--; rebuildWidgets(); })
                .bounds(width / 2 - 150, height - 28, 95, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cancel"), button -> onClose())
                .bounds(width / 2 - 50, height - 28, 100, 20).build());
        if ((page + 1) * PAGE_SIZE < available.size()) addRenderableWidget(Button.builder(Component.literal("Next >"), button -> { page++; rebuildWidgets(); })
                .bounds(width / 2 + 55, height - 28, 95, 20).build());
    }

    @Override
    public void onClose() { minecraft.setScreenAndShow(parent); }

    @Override
    public boolean isPauseScreen() { return false; }
}
