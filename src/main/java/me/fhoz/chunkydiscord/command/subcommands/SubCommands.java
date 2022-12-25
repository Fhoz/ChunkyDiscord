package me.fhoz.chunkydiscord.command.subcommands;

import me.fhoz.chunkydiscord.ChunkyDiscord;
import me.fhoz.chunkydiscord.command.CDCommand;
import me.fhoz.chunkydiscord.command.CDSubCommand;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public class SubCommands {
    private SubCommands () {}

    @Nonnull
    public static Collection<CDSubCommand> getAllCommands(@Nonnull CDCommand cmd) {
        ChunkyDiscord plugin = cmd.getPlugin();
        return List.of(
            new ChannelCommand(plugin, cmd),
            new HelpCommand(plugin, cmd),
            new IntervalCommand(plugin, cmd)
        );
    }
}
