package com.automaton.macro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MacroManager {
    private static final List<MacroProfile> macros = new ArrayList<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static volatile boolean isRunning = false;

    public static void loadMacros(Path configDir) {
        macros.clear();
        Path macroDir = configDir.resolve("automaton/macros");
        try {
            if (!Files.exists(macroDir)) {
                Files.createDirectories(macroDir);
                createDefaultMacro(macroDir);
            }
            File folder = macroDir.toFile();
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    try (FileReader reader = new FileReader(file)) {
                        MacroProfile profile = GSON.fromJson(reader, MacroProfile.class);
                        if (profile != null) macros.add(profile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultMacro(Path macroDir) {
        try {
            String defaultJson = """
            {
              "name": "Custom Combo Macro",
              "description": "Switch to slot 7, attack, wait, switch slot 3, click.",
              "actions": [
                { "type": "HOTBAR", "value": 7 },
                { "type": "ATTACK", "value": 0 },
                { "type": "WAIT", "value": 100 },
                { "type": "HOTBAR", "value": 3 },
                { "type": "CLICK", "value": 0 }
              ]
            }
            """;
            Files.writeString(macroDir.resolve("example_combo.json"), defaultJson);
        } catch (Exception ignored) {}
    }

    public static List<MacroProfile> getMacros() { return macros; }

    public static void executeMacro(MacroProfile profile, MinecraftClient client) {
        if (isRunning || client.player == null) return;
        isRunning = true;

        CompletableFuture.runAsync(() -> {
            try {
                for (MacroAction action : profile.getActions()) {
                    if (!isRunning || client.player == null) break;

                    client.execute(() -> {
                        switch (action.getType()) {
                            case HOTBAR -> {
                                if (action.getValue() >= 1 && action.getValue() <= 9) {
                                    client.player.getInventory().selectedSlot = action.getValue() - 1;
                                }
                            }
                            case ATTACK -> {
                                if (client.interactionManager != null && client.targetedEntity != null) {
                                    client.interactionManager.attackEntity(client.player, client.targetedEntity);
                                    client.player.swingHand(Hand.MAIN_HAND);
                                }
                            }
                            case CLICK -> {
                                if (client.interactionManager != null) {
                                    client.interactionManager.attackBlock(client.player.getBlockPos(), client.player.getHorizontalFacing());
                                }
                            }
                            case RIGHT_CLICK -> {
                                if (client.interactionManager != null) {
                                    client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
                                }
                            }
                            case WAIT -> {}
                        }
                    });

                    int sleepTime = action.getType() == MacroAction.Type.WAIT ? action.getValue() : 50;
                    Thread.sleep(Math.max(sleepTime, 10));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                isRunning = false;
            }
        });
    }

    public static void emergencyStop() {
        isRunning = false;
    }
}
