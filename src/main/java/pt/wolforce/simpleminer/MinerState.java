package pt.wolforce.simpleminer;

import net.minecraft.util.StringRepresentable;

public enum MinerState implements StringRepresentable {
    OFF("off"),
    STUCK("stuck"),
    ON("on");

    private final String name;

    private MinerState(String name) {
        this.name = name;
    }

    public String toString() {
        return this.getSerializedName();
    }

    public String getSerializedName() {
        return this.name;
    }
}
