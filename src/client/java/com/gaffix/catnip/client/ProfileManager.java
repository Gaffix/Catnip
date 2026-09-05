package com.gaffix.catnip.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ProfileManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("catnip-keybind-profiles.json");
    private static final ProfileManager INSTANCE = new ProfileManager();
    public List<KeybindProfile> profiles = new ArrayList<>();

    private ProfileManager() {}

    public static ProfileManager get() {
        return INSTANCE;
    }

    public void load(Minecraft minecraft) {
        boolean changed = false;
        if (Files.exists(FILE)) {
            try (Reader reader = Files.newBufferedReader(FILE)) {
                ProfileManager loaded = GSON.fromJson(reader, ProfileManager.class);
                if (loaded != null && loaded.profiles != null) profiles = loaded.profiles;
            } catch (Exception exception) {
                CatnipClient.LOGGER.error("Could not load keybind profiles", exception);
            }
        }
        if (profiles.isEmpty() || !"Default".equals(profiles.getFirst().name)) {
            KeybindProfile defaults = captureDefaults(minecraft);
            profiles.removeIf(profile -> "Default".equals(profile.name));
            profiles.addFirst(defaults);
            changed = true;
        }
        for (KeybindProfile profile : profiles) {
            changed |= profile.bindings.remove(CatnipClient.OPEN_PROFILES_KEY) != null;
        }
        if (changed) save();
    }

    private KeybindProfile captureDefaults(Minecraft minecraft) {
        KeybindProfile profile = new KeybindProfile("Default");
        for (KeyMapping mapping : minecraft.options.keyMappings) {
            if (CatnipClient.canBeProfiled(mapping)) {
                profile.bindings.put(mapping.getName(), mapping.getDefaultKey().getName());
            }
        }
        return profile;
    }

    public void apply(KeybindProfile profile, Minecraft minecraft) {
        for (KeyMapping mapping : minecraft.options.keyMappings) {
            if (!CatnipClient.canBeProfiled(mapping)) continue;
            String savedKey = profile.bindings.get(mapping.getName());
            if (savedKey != null) mapping.setKey(InputConstants.getKey(savedKey));
        }
        KeyMapping.resetMapping();
        minecraft.options.save();
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException exception) {
            CatnipClient.LOGGER.error("Could not save keybind profiles", exception);
        }
    }
}
