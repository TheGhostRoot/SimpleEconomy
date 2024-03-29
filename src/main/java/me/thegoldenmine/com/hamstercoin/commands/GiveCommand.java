package me.thegoldenmine.com.hamstercoin.commands;

import me.thegoldenmine.com.hamstercoin.HexCoin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class GiveCommand implements CommandExecutor {
    private final HexCoin plugin;
    public GiveCommand(HexCoin plugin){
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("hex.give")) {
                player.sendMessage(plugin.balances.getMissingPermissionsMessage("hex.give"));
                return true;
            }

            if (args.length < 2) {
                player.sendMessage(plugin.balances.getMissingArgumentMessage("<player name> <money>"));
                return true;
            }

            Player peyee = Bukkit.getPlayer(String.valueOf(args[0]));
            if (peyee == null) {
                return true;
            }

            plugin.getLogger().info(args.toString());

            double tempMoney;
            try {
                tempMoney = Double.parseDouble(String.valueOf(args[1]));
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }

            plugin.balances.give(peyee.getUniqueId(), tempMoney);

            if (peyee.isOnline()) {
                peyee.sendMessage(plugin.balances.getYouGotPayedMessage(tempMoney, player.getDisplayName()));
            }

            player.sendMessage(plugin.balances.getYouPayedMessage(tempMoney, peyee.getDisplayName()));
            return true;
        }


        if (sender instanceof ConsoleCommandSender) {
            if (args.length < 2) {
                return true;
            }

            Player peyee = Bukkit.getPlayer(String.valueOf(args[0]));
            if (peyee == null) {
                return true;
            }

            double tempMoney;
            try {
                tempMoney = Double.parseDouble(String.valueOf(args[1]));
            } catch (Exception e) {
                return true;
            }

            plugin.balances.give(peyee.getUniqueId(), tempMoney);

            if (peyee.isOnline()) {
                peyee.sendMessage(plugin.balances.getYouGotPayedMessage(tempMoney, plugin.balances.getConsoleName()));
            }
        }
        return true;
    }
}
