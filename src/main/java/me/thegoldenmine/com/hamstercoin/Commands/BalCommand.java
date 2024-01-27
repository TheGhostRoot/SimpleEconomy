package me.thegoldenmine.com.hamstercoin.Commands;

import me.thegoldenmine.com.hamstercoin.HamsterCoin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BalCommand implements CommandExecutor {
    private HamsterCoin plugin;

    public BalCommand(HamsterCoin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("hamster.bal")) {
                UUID playerUUID = player.getUniqueId();
                int bal = plugin.getBalances().getBalance(playerUUID);
                String balStr = String.valueOf(bal);
                player.sendMessage(ChatColor.GREEN + "Your balance is " + ChatColor.GOLD + balStr);
            } else {
                player.sendMessage(ChatColor.RED + "You don't have " + ChatColor.GOLD + "hamster.bal");
            }
        } else {
            sender.sendMessage("Only players can see their balance");
        }
        return true;
    }
}
