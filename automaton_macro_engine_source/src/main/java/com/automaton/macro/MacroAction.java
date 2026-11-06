package com.automaton.macro;

public class MacroAction {
    public enum Type { CLICK, RIGHT_CLICK, ATTACK, HOTBAR, WAIT }

    private Type type;
    private int value;

    public MacroAction() {}

    public MacroAction(Type type, int value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() { return type; }
    public int getValue() { return value; }
}
