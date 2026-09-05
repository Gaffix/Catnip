package com.gaffix.catnip.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class ProfileEditorScreen extends Screen {
    private static final int PAGE_SIZE = 7;
    private final Screen parent;
    private final KeybindProfile profile;
    private int page;
    private KeyMapping listening;

    public ProfileEditorScreen(Screen parent, KeybindProfile profile) {
        super(Component.literal("Edit " + profile.name));
        this.parent = parent;
        this.profile = profile;
    }

    @Override
    protected void init() {
        List<KeyMapping> mappings = selectedMappings();
        int start = page * PAGE_SIZE;
        int y = 32;
        if (!"Default".equals(profile.name)) {
            EditBox name = new EditBox(font, width / 2 - 155, 8, 310, 20, Component.literal("Profile name"));
            name.setMaxLength(32);
            name.setValue(profile.name);
            name.setResponder(value -> {
                String trimmed = value.trim();
                if (!trimmed.isEmpty()) profile.name = trimmed;
                ProfileManager.get().save();
            });
            addRenderableWidget(name);
        }
        for (int i = start; i < Math.min(start + PAGE_SIZE, mappings.size()); i++) {
            KeyMapping mapping = mappings.get(i);
            Component name = Component.translatable(mapping.getName());
            addRenderableWidget(Button.builder(name, button -> {})
                    .bounds(width / 2 - 155, y, 160, 20).build()).active = false;
            String value = profile.bindings.get(mapping.getName());
            addRenderableWidget(Button.builder(Component.literal(InputConstants.getKey(value).getDisplayName().getString()), button -> {
                listening = mapping;
                button.setMessage(Component.literal("> press a key <"));
            }).bounds(width / 2 + 10, y, 115, 20).build());
            addRenderableWidget(Button.builder(Component.literal("X"), button -> {
                profile.bindings.remove(mapping.getName());
                ProfileManager.get().save();
                rebuildWidgets();
            }).bounds(width / 2 + 130, y, 25, 20).build());
            y += 24;
        }

        addRenderableWidget(Button.builder(Component.literal("Add binding"), button ->
                minecraft.setScreenAndShow(new BindingPickerScreen(this, profile)))
                .bounds(width / 2 - 155, height - 52, 100, 20).build());
        if (page > 0) addRenderableWidget(Button.builder(Component.literal("<"), button -> { page--; rebuildWidgets(); })
                .bounds(width / 2 - 50, height - 52, 45, 20).build());
        if ((page + 1) * PAGE_SIZE < mappings.size()) addRenderableWidget(Button.builder(Component.literal(">"), button -> { page++; rebuildWidgets(); })
                .bounds(width / 2 + 5, height - 52, 45, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                .bounds(width / 2 + 55, height - 52, 100, 20).build());

        if (!"Default".equals(profile.name)) {
            addRenderableWidget(Button.builder(Component.literal("Delete profile"), button -> {
                ProfileManager.get().profiles.remove(profile);
                ProfileManager.get().save();
                minecraft.setScreenAndShow(parent);
            }).bounds(width / 2 - 75, height - 28, 150, 20).build());
        }
    }

    private List<KeyMapping> selectedMappings() {
        return java.util.Arrays.stream(minecraft.options.keyMappings)
                .filter(CatnipClient::canBeProfiled)
                .filter(mapping -> profile.bindings.containsKey(mapping.getName())).toList();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (listening != null) {
            profile.bindings.put(listening.getName(), InputConstants.getKey(event).getName());
            listening = null;
            ProfileManager.get().save();
            rebuildWidgets();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (listening != null) {
            profile.bindings.put(listening.getName(),
                    InputConstants.Type.MOUSE.getOrCreate(event.button()).getName());
            listening = null;
            ProfileManager.get().save();
            rebuildWidgets();
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void onClose() {
        ProfileManager.get().save();
        minecraft.setScreenAndShow(parent);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
