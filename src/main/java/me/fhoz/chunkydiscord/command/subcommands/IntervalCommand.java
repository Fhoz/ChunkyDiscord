package me.fhoz.chunkydiscord.command.subcommands;

import io.github.bakedlibs.dough.common.ChatColors;
import me.fhoz.chunkydiscord.ChunkyDiscord;
import me.fhoz.chunkydiscord.command.CDCommand;
import me.fhoz.chunkydiscord.command.CDSubCommand;
import me.fhoz.chunkydiscord.command.Permission;
import me.fhoz.chunkydiscord.util.ChunkyUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class IntervalCommand extends CDSubCommand {
    @ParametersAreNonnullByDefault
    IntervalCommand(ChunkyDiscord plugin, CDCommand cmd) {
        super(plugin, cmd, "interval", false);
    }

    @Override
    public void onExecute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (sender instanceof Player p) {
            if (!hasPermission(p, Permission.INTERVAL)) {
                sender.sendMessage(ChatColors.color("&cYou do not have permission to use this command!"));
                return;
            }
        }
        if (args.length == 1) {
            sender.sendMessage(ChatColors.color("&cUsage: /chunkydiscord interval <ticks>"));
            return;
        }
        try {
            final long ticks = Long.parseLong(args[1]);
            if (ticks <= 0) {
                sender.sendMessage(ChatColors.color("&cInterval must be greater than 0!"));
                return;
            }
            if (ticks < 40) {
                sender.sendMessage(ChatColors.color("&eWarning: The plugin may not be able to keep up with an interval of less than 40 ticks, consider increasing it if you see &f[ChunkyDiscord] Failed to update progress message &ebeing spammed in the console."));
            }
            plugin.getConfigUtil().setUpdateInterval(ticks).save();
            ChunkyUtil.updateBukkitTask();
            sender.sendMessage(ChatColors.color("&aUpdate interval set to " + ticks + " ticks!"));
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColors.color("&cThe provided interval is not a valid number!"));
        }
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Set the update interval in ticks per task.";
    }
}
