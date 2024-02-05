package me.thegoldenmine.com.hamstercoin.commands;

import me.thegoldenmine.com.hamstercoin.HexCoin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadHexCommand implements CommandExecutor {

    private HexCoin plugin;

    public ReloadHexCommand(HexCoin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("hex.reload")) {
                plugin.balances.reload();
                player.sendMessage("reloaded!");

            } else {
                player.sendMessage(plugin.balances.getMissingPermissionsMessage("hex.reload"));
            }
        }
        return true;
    }
}
