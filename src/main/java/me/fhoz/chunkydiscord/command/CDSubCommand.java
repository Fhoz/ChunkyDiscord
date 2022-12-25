package me.fhoz.chunkydiscord.command;

import me.fhoz.chunkydiscord.ChunkyDiscord;
import me.fhoz.chunkydiscord.util.Check;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Map;

public abstract class CDSubCommand {
    protected final ChunkyDiscord plugin;
    protected final CDCommand cmd;
    private final String name;
    private final boolean hidden;

    @ParametersAreNonnullByDefault
    protected CDSubCommand(ChunkyDiscord plugin, CDCommand cmd, String name, boolean hidden) {
        this.plugin = plugin;
        this.cmd = cmd;
        this.name = name;
        this.hidden = hidden;
    }

    @Nonnull
    public final String getName() {
        return name;
    }

    public final boolean isHidden() {
        return hidden;
    }

    protected void recordUsage(@Nonnull Map<CDSubCommand, Integer> commandUsage) {
        commandUsage.merge(this, 1, Integer::sum);
    }

    public abstract void onExecute(@Nonnull CommandSender sender, @Nonnull String[] args);

    @Nonnull
    protected String getDescription() {
        return "commands." + getName();
    }

    @Nonnull
    public String getDescription(@Nonnull CommandSender sender) {
        if (sender instanceof Player player) {
            Check.notNull(player, "Player must not be null!");
            Check.notNull(getDescription(), "Message key must not be null!");
        }
        return getDescription();
    }

    public boolean hasPermission(Player p, Permission ... permissions) {
        return Arrays.stream(permissions).filter(perm -> p.hasPermission(perm.getPermission())).count() == permissions.length;
    }
}
