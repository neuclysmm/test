package com.automaton.gui;

import com.automaton.macro.MacroManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.nio.file.Files;
import java.nio.file.Path;

public class MacroEditorScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget nameField;
    private TextFieldWidget slotField;

    public MacroEditorScreen(Screen parent) {
        super(Text.literal("Create New Macro"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        this.nameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 60, 200, 20, Text.literal("Macro Name"));
        this.nameField.setMaxLength(30);
        this.nameField.setText("custom_macro");
        this.addDrawableChild(this.nameField);

        this.slotField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 120, 200, 20, Text.literal("Hotbar Slot"));
        this.slotField.setMaxLength(2);
        this.slotField.setText("7");
        this.addDrawableChild(this.slotField);

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("§aSave Macro JSON"),
            button -> saveMacroToFile()
        ).dimensions(this.width / 2 - 100, 170, 200, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
            Text.literal("Back"),
            button -> this.client.setScreen(this.parent)
        ).dimensions(this.width / 2 - 100, 200, 200, 20).build());
    }

    private void saveMacroToFile() {
        String fileName = nameField.getText().trim().replaceAll("[^a-zA-Z0-9_]", "_");
        String slotStr = slotField.getText().trim();
        if (fileName.isEmpty()) return;

        int slot = 7;
        try {
            slot = Integer.parseInt(slotStr);
        } catch (NumberFormatException ignored) {}

        try {
            Path configDir = FabricLoader.getInstance().getConfigDir();
            Path macroDir = configDir.resolve("automaton/macros");
            Files.createDirectories(macroDir);

            String jsonContent = String.format("""
            {
              "name": "%s",
              "description": "Created via in-game GUI editor",
              "actions": [
                { "type": "HOTBAR", "value": %d },
                { "type": "ATTACK", "value": 0 },
                { "type": "WAIT", "value": 100 },
                { "type": "HOTBAR", "value": 3 },
                { "type": "CLICK", "value": 0 }
              ]
            }
            """, fileName, slot);

            Files.writeString(macroDir.resolve(fileName + ".json"), jsonContent);
            MacroManager.loadMacros(configDir);

            if (this.client != null && this.client.player != null) {
                this.client.player.sendMessage(Text.literal("§aSaved: config/automaton/macros/" + fileName + ".json"), true);
            }
            this.client.setScreen(this.parent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.literal("File Name:"), this.width / 2 - 100, 48, 0xA0A0A0);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Starting Hotbar Slot (1-9):"), this.width / 2 - 100, 108, 0xA0A0A0);
        super.render(context, mouseX, mouseY, delta);
    }
}
