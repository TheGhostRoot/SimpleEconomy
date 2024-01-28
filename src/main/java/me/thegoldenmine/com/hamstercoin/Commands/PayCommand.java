package me.thegoldenmine.com.hamstercoin.Commands;

import me.thegoldenmine.com.hamstercoin.Balances;
import me.thegoldenmine.com.hamstercoin.HexCoin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class PayCommand implements CommandExecutor {

    private final HexCoin plugin;

    public PayCommand(HexCoin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            // /payhex <playername> <money>
            Player player = (Player) sender;
            if (!player.hasPermission("hex.pay")) {
                player.sendMessage(plugin.balances.getMissingPermissionsMessage("hex.pay"));
                return true;
            }

            if (args.length < 2) {
                player.sendMessage(plugin.balances.getMissingArgumentMessage("<player name> <money>"));
                return true;
            }

            Player res = Bukkit.getPlayer(args[0]);
            if (res == null) {
                return true;
            }

            double tempMoney;
            try {
                tempMoney = Double.parseDouble(String.valueOf(args[1]));
            } catch (Exception e) {
                return true;
            }

            plugin.balances.pay(player, res, tempMoney);
            return true;
        }
        return true;
    }
}
