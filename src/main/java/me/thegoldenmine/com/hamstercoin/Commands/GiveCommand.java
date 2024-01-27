package me.thegoldenmine.com.hamstercoin.Commands;

import me.thegoldenmine.com.hamstercoin.HamsterCoin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GiveCommand implements CommandExecutor {
    private final HamsterCoin plugin;
    public GiveCommand(HamsterCoin plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("hamstercoin.give")) {
                player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You don't have " + ChatColor.GOLD + "hamstercoin.give" + ChatColor.RED + "" + ChatColor.ITALIC + " permission!");
                return true;
            }
            
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "/gc help | /givecoin help");
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.BLUE + "<-=-=--[" + ChatColor.GOLD + "Hamster Coin" + ChatColor.BLUE + "]=-=--=>");
            sender.sendMessage(ChatColor.GOLD + " /givecoin <playername> <money>");
            sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You can give money to online and offline players. You must give a number without any spaces and words!");
            return true;
        }

        Player payee = Bukkit.getPlayer(args[0]);
        if (payee == null) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You have to give a player name and then the money you want to pay!");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You have to give a amount  money to be payed!");
            return true;
        }

        String moneyStr = args[1];
        int money = 0;
        try {
            money = Integer.parseInt(moneyStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "You can only give a number!");
            return true;
        }

        UUID payeeUuid = payee.getUniqueId();
        plugin.getBalances().give(payeeUuid, money);
        payee.sendMessage(ChatColor.GREEN+"You have given "+ChatColor.GOLD+moneyStr+ChatColor.GREEN+" to "+ChatColor.GOLD+payee.getName()+ChatColor.GREEN+" player!"); // the one that gives the money
        plugin.getBalances().save();
        plugin.getBalances().reload();
        payee.sendMessage(ChatColor.GREEN+"Your balance is successfully save in the config file.");

        return true;
    }
}
