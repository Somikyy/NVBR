package org.nvbr.commands;


import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.nvbr.managers.GlobalChatManager;

@RequiredArgsConstructor
public class GlobalChatCommand implements CommandExecutor {
    private final GlobalChatManager globalChatManager;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("Usage: /globalchat <message>");
            return true;
        }

        Player player = (Player) sender;
        String message = String.join(" ", args);
        globalChatManager.sendMessage(player, message);
        return true;
    }
}