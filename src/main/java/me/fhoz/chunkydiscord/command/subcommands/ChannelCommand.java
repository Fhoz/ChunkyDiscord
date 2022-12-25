package me.fhoz.chunkydiscord.command.subcommands;

import io.github.bakedlibs.dough.common.ChatColors;
import me.fhoz.chunkydiscord.ChunkyDiscord;
import me.fhoz.chunkydiscord.command.CDCommand;
import me.fhoz.chunkydiscord.command.CDSubCommand;
import me.fhoz.chunkydiscord.command.Permission;
import me.fhoz.chunkydiscord.util.DiscordUtil;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ChannelCommand extends CDSubCommand {
    @ParametersAreNonnullByDefault
    ChannelCommand(ChunkyDiscord plugin, CDCommand cmd) {
        super(plugin, cmd, "channel", false);
    }

    @Override
    public void onExecute(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (sender instanceof Player p) {
            if (!hasPermission(p, Permission.CHANNEL)) {
                sender.sendMessage(ChatColors.color("&cYou do not have permission to use this command!"));
                return;
            }
        }
        if (args.length == 1) {
            sender.sendMessage(ChatColors.color("&cUsage: /chunkydiscord channel <id>"));
            return;
        }
        final String id = args[1];
        try {
            final long channelId = Long.parseLong(id);
            final TextChannel channel = plugin.getJda().getTextChannelById(channelId);
            if (channel == null) {
                sender.sendMessage(ChatColors.color("&cChannel with id " + id + " not found!"));
                return;
            }
            DiscordUtil.clearMessageCache();
            plugin.getConfigUtil().setChannel(channelId).save();
            sender.sendMessage(ChatColors.color("&aChannel set to " + channel.getName()) + " and message cache cleared!");
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColors.color("&cInvalid channel ID!"));
        }
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Set the channel to send messages to.";
    }
}
