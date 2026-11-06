package com.automaton.macro;

import java.util.List;

public class MacroProfile {
    private String name;
    private String description;
    private List<MacroAction> actions;

    public MacroProfile() {}

    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<MacroAction> getActions() { return actions; }
}
