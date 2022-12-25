package me.fhoz.chunkydiscord.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

public class CDTabCompleter implements TabCompleter {
    private static int MAX_SUGGESTIONS = 80;
    private final CDCommand command;

    public CDTabCompleter(CDCommand command) {
        this.command = command;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            return createReturnList(command.getSubCommandNames(), args[0]);
        }
        switch (args[0].toUpperCase(Locale.ROOT)) {
            case "CHANNEL" -> {
                return createReturnList(args[1], "<id>");
            }
            case "INTERVAL" -> {
                return createReturnList(args[1], "<ticks>");
            }
        }
        return null;
    }

    @Nonnull
    private List<String> createReturnList(@Nonnull List<String> list, @Nonnull String string) {
        if (string.length() == 0) {
            return list;
        }

        String input = string.toLowerCase(Locale.ROOT);
        List<String> returnList = new LinkedList<>();

        for (String item : list) {
            if (item.toLowerCase(Locale.ROOT).contains(input)) {
                returnList.add(item);

                if (returnList.size() >= MAX_SUGGESTIONS) {
                    break;
                }
            } else if (item.equalsIgnoreCase(input)) {
                return Collections.emptyList();
            }
        }
        return returnList;
    }

    private List<String> createReturnList(@Nonnull String arg, @Nonnull String ... strings) {
        return createReturnList(List.of(strings), arg);
    }
}
