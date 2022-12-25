package me.fhoz.chunkydiscord.command;

public enum Permission {
    CHANNEL("chunkydiscord.command.channel"),
    INTERVAL("chunkydiscord.command.interval");

    private final String permission;
    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
