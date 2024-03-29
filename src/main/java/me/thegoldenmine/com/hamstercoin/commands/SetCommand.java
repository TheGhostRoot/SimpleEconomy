package me.thegoldenmine.com.hamstercoin.commands;

import me.thegoldenmine.com.hamstercoin.HexCoin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;


public class SetCommand implements CommandExecutor {
    public HexCoin plugin;

    public SetCommand(HexCoin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //do stuff
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("hex.set")) {
                player.sendMessage(plugin.balances.getMissingPermissionsMessage("hex.set"));
                return true;
            }

            // /sethex <playername> <money>
            if (args.length < 2) {
                player.sendMessage(plugin.balances.getMissingArgumentMessage("<player name> <money>"));
                return true;
            }

            Player playerToSet = Bukkit.getPlayer(args[0]);
            if (playerToSet == null) {
                return true;
            }

            double tempMoney;
            try {
                tempMoney = Double.parseDouble(String.valueOf(args[1]));
            } catch (Exception e) {
                return true;
            }

            plugin.balances.setBalance(playerToSet.getUniqueId(), tempMoney);
            player.sendMessage(plugin.balances.getSetBalMessage(playerToSet.getDisplayName(), tempMoney));

        } else if (sender instanceof ConsoleCommandSender) {
            if (args.length < 2) {
                plugin.getLogger().info(plugin.balances.getMissingArgumentMessage("<player name> <money>"));
                return true;
            }

            Player playerToSet = Bukkit.getPlayer(args[0]);
            if (playerToSet == null) {
                return true;
            }

            double tempMoney;
            try {
                tempMoney = Double.parseDouble(String.valueOf(args[1]));
            } catch (Exception e) {
                return true;
            }

            plugin.balances.setBalance(playerToSet.getUniqueId(), tempMoney);
        }
        return true;
    }
}
