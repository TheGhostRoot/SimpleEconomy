package me.thegoldenmine.com.hamstercoin.Commands;

import me.thegoldenmine.com.hamstercoin.HamsterCoin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;


public class PayCommand implements CommandExecutor {

    private final HamsterCoin plugin;

    public PayCommand(HamsterCoin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "/pc help | /paycoin help");
            }
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("hamstercoin.pay")) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You don't have " + ChatColor.GOLD + "hamstercoin.pay" + ChatColor.RED + "" + ChatColor.ITALIC + " permission!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.GOLD + " /paycoin <playername> <money>");
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            player.sendMessage(ChatColor.BLUE + "<-=-=--[" + ChatColor.GOLD + "Hamster Coin" + ChatColor.BLUE + "]=-=--=>");
            player.sendMessage(ChatColor.GOLD + " /paycoin <playername> <money>");
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You can give money to online and offline players. You must give a number without any spaces and words!");
            return true;
        }

        Player payee = Bukkit.getPlayer(args[0]);
        if (payee == null) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You have to give a player name and then the money you want to pay!");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You have to give a amount  money to be payed!");
            return true;
        }

        String moneyStr = args[1];
        int money = 0;
        try {
            money = Integer.parseInt(moneyStr);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You can only give a number!");
            return true;
        }

        try {
            UUID payerUuid = player.getUniqueId();
            UUID payeeUuid = payee.getUniqueId();
            plugin.getBalances().pay(payerUuid, payeeUuid, money);
            plugin.getBalances().save();
            plugin.getBalances().reload();
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Internal error saving player balance!");
        }

        return true;
    }
}
