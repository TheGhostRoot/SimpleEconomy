package me.thegoldenmine.com.hamstercoin.Commands;

import me.thegoldenmine.com.hamstercoin.HamsterCoin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ResetCommand implements CommandExecutor {

    private final HamsterCoin plugin;

    public ResetCommand(HamsterCoin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("hamstercoin.reset")) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You don't have " + ChatColor.GOLD + "hamstercoin.reset" + ChatColor.RED + "" + ChatColor.ITALIC + " permission!");
                return true;
            }

        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "/rc help | /reset help");
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.BLUE + "<-=-=--[" + ChatColor.GOLD + "Hamster Coin" + ChatColor.BLUE + "]=-=--=>");
            sender.sendMessage(ChatColor.GOLD + " /resetcoin <playername>");
            sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You can reset money to online and offline players.");
            return true;
        }

        Player payee = Bukkit.getPlayer(args[0]);
        if (payee == null) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You have to give a player name!");
            return true;
        }

        UUID payeeUuid = payee.getUniqueId();
        plugin.getBalances().setBalance(payeeUuid, 0);
        payee.sendMessage(ChatColor.RED+"Your balance was reset and now your balance is "+ChatColor.GOLD+plugin.getBalances().getBalance(payeeUuid)+ChatColor.RED+" money!");
        plugin.getBalances().save();
        plugin.getBalances().reload();
        payee.sendMessage(ChatColor.GREEN+"Your balance is successfully save in the config file.");
        return true;
    }
}
