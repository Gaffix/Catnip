package com.gaffix.catnip.client;

import java.util.LinkedHashMap;
import java.util.Map;

public final class KeybindProfile {
    public String name;
    public Map<String, String> bindings = new LinkedHashMap<>();

    public KeybindProfile(String name) {
        this.name = name;
    }
}
