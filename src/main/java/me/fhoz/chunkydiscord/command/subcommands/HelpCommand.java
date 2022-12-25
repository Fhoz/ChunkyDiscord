package me.fhoz.chunkydiscord.command.subcommands;

import me.fhoz.chunkydiscord.ChunkyDiscord;
import me.fhoz.chunkydiscord.command.CDCommand;
import me.fhoz.chunkydiscord.command.CDSubCommand;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class HelpCommand extends CDSubCommand {
    @ParametersAreNonnullByDefault
    HelpCommand(ChunkyDiscord plugin, CDCommand cmd) {
        super(plugin, cmd, "help", false);
    }

    @Override
    public void onExecute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        cmd.sendHelp(sender);
    }

    @Override
    @Nonnull
    public String getDescription() {
        return "Shows this help message.";
    }
}
