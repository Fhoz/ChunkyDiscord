package me.fhoz.chunkydiscord.command;

import io.github.bakedlibs.dough.common.ChatColors;
import me.fhoz.chunkydiscord.ChunkyDiscord;
import me.fhoz.chunkydiscord.command.subcommands.SubCommands;
import me.fhoz.chunkydiscord.util.Check;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

public class CDCommand implements CommandExecutor, Listener {
    private boolean registered = false;
    private final ChunkyDiscord plugin;
    private final List<CDSubCommand> commands = new LinkedList<>();
    private final Map<CDSubCommand, Integer> commandUsage = new HashMap<>();

    public CDCommand(@Nonnull ChunkyDiscord plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Check.isTrue(!registered, "ChunkyDiscord's subcommands have already been registered!");
        registered = true;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("chunkydiscord").setExecutor(this);
        plugin.getCommand("chunkydiscord").setTabCompleter(new CDTabCompleter(this));
        commands.addAll(SubCommands.getAllCommands(this));
    }

    public @Nonnull ChunkyDiscord getPlugin() {
        return plugin;
    }

    public @Nonnull Map<CDSubCommand, Integer> getCommandUsage() {
        return commandUsage;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            for (CDSubCommand command : commands) {
                if (args[0].equalsIgnoreCase(command.getName())) {
                    command.recordUsage(commandUsage);
                    command.onExecute(sender, args);
                    return true;
                }
            }
        }
        sendHelp(sender);

        return !commands.isEmpty();
    }

    public void sendHelp(@Nonnull CommandSender sender) {
        sender.sendMessage(ChatColors.color("&8&m----------------------------------------"));
        sender.sendMessage(ChatColors.color("&6&lChunkyDiscord &8- &7Commands"));
        sender.sendMessage(ChatColors.color("&8&m----------------------------------------"));
        for (CDSubCommand command : commands) {
            if (!command.isHidden()) {
                sender.sendMessage(ChatColors.color("&6/chunkydiscord " + command.getName() + " &8- &7" + command.getDescription(sender)));
            }
        }
    }

    @EventHandler
    @ParametersAreNonnullByDefault
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equalsIgnoreCase("/help chunkydiscord")) {
            sendHelp(event.getPlayer());
            event.setCancelled(true);
        }
    }

    public @Nonnull List<String> getSubCommandNames() {
        List<String> names = new LinkedList<>();
        for (CDSubCommand command : commands) {
            names.add(command.getName());
        }
        return names;
    }
}
