# Catnip

Catnip is a lightweight client-side Fabric mod for quickly switching between
Minecraft keybind layouts. It is designed with Hypixel SkyBlock in mind, where
different activities often benefit from different control setups.

## Features

- A permanent **Default** profile containing your original controls
- Custom profiles with only the bindings you want to override
- Support for keyboard keys and mouse buttons, including extra side buttons
- Support for vanilla controls and keybinds added by other mods
- Persistent profiles that remain available after restarting Minecraft

## Usage

1. Press `P` to open the Catnip profile menu. You can change this shortcut under
   Minecraft's Controls settings.
2. Select **New profile**, give it a name, and choose **Add binding**.
3. Pick a control, click its current assignment, and press the new keyboard or
   mouse button.
4. Return to the profile list and select **Use _profile name_** to apply it.

Profiles are stored in `config/catnip-keybind-profiles.json`.

## Requirements

- Minecraft 26.2
- Fabric Loader 0.19.3 or newer
- Fabric API
- Java 25 or newer

## Building

```shell
./gradlew build
```

The compiled mod will be written to `build/libs/`.

## License

Catnip is available under the [CC0 1.0 Universal license](LICENSE).
