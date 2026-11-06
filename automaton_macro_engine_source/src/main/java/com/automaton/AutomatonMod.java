package com.automaton;

import com.automaton.gui.MacroSelectionScreen;
import com.automaton.macro.MacroManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;

public class AutomatonMod implements ClientModInitializer {
    private static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.automaton.menu",
            InputUtil.Type.KEY_SYM,
            GLFW.GLFW_KEY_M,
            "category.automaton.macros"
        ));

        Path configDir = FabricLoader.getInstance().getConfigDir();
        MacroManager.loadMacros(configDir);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                client.setScreen(new MacroSelectionScreen(client.currentScreen));
            }
        });
    }
}
