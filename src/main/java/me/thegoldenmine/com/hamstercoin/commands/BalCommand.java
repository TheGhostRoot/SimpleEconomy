package me.thegoldenmine.com.hamstercoin.commands;

import me.thegoldenmine.com.hamstercoin.HexCoin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalCommand implements CommandExecutor {
    private HexCoin plugin;

    public BalCommand(HexCoin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("hex.bal")) {
                player.sendMessage(plugin.balances.getYourBalanceMessage(plugin.balances.getBalance(player.getUniqueId())));

            } else {
                player.sendMessage(plugin.balances.getMissingPermissionsMessage("hex.bal"));
            }
        }
        return true;
    }
}
