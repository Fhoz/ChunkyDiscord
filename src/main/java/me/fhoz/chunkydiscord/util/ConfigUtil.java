package me.fhoz.chunkydiscord.util;

import me.fhoz.chunkydiscord.ChunkyDiscord;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConfigUtil {
    private final ChunkyDiscord plugin;

    public ConfigUtil(final ChunkyDiscord plugin) {
        this.plugin = plugin;
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }

    @Nullable
    public String getToken() {
        return plugin.getConfig().getString("bot.token");
    }

    public String getChannel() {
        return plugin.getConfig().getString("bot.channel");
    }

    @Nonnull
    public String getName() {
        return plugin.getConfig().getString("bot.name", "Chunky");
    }

    public long getUpdateInterval() {
        return plugin.getConfig().getLong("update-every-x-ticks", 100L);
    }

    public boolean isDebugEnabled() {
        return plugin.getConfig().getBoolean("debug", false);
    }

    public ConfigUtil setChannel(long channel) {
        plugin.getConfig().set("bot.channel", channel);
        return this;
    }

    public ConfigUtil setUpdateInterval(long ticks) {
        plugin.getConfig().set("update-every-x-ticks", ticks);
        return this;
    }

    public void save() {
        plugin.saveConfig();
    }
}
