package com.automaton.gui;

import com.automaton.macro.MacroManager;
import com.automaton.macro.MacroProfile;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class MacroSelectionScreen extends Screen {
    private final Screen parent;

    public MacroSelectionScreen(Screen parent) {
        super(Text.literal("Automaton Multi-Macro Dashboard"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int yOffset = 40;
        for (MacroProfile profile : MacroManager.getMacros()) {
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Run: " + profile.getName()),
                button -> {
                    MacroManager.executeMacro(profile, this.client);
                    if (this.client != null && this.client.player != null) {
                        this.client.player.sendMessage(Text.literal("Executing: " + profile.getName()), true);
                    }
                }
            ).dimensions(this.width / 2 - 100, yOffset, 200, 20).build());
            yOffset += 24;
        }

        // Add New Macro Button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§e+ Create New Macro"),
            button -> this.client.setScreen(new MacroEditorScreen(this))
        ).dimensions(this.width / 2 - 100, yOffset + 10, 200, 20).build());

        // Emergency Stop Button
        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§cEmergency Stop"),
            button -> MacroManager.emergencyStop()
        ).dimensions(this.width / 2 - 100, this.height - 35, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}
