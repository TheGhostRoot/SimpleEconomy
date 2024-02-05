package me.thegoldenmine.com.hamstercoin.commands;

import me.thegoldenmine.com.hamstercoin.HexCoin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.nio.Buffer;
import java.util.UUID;

public class TakeCommand implements CommandExecutor {
    public HexCoin plugin;

    public TakeCommand(HexCoin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player || sender instanceof ConsoleCommandSender) {
            // command playername money
            if (args.length < 2) {
                return true;
            }

            Player playerToTakeMoneyFrom = Bukkit.getPlayer(args[0]);
            if (playerToTakeMoneyFrom == null) {
                return true;
            }

            try {
                UUID uuid = playerToTakeMoneyFrom.getUniqueId();
                double money = plugin.balances.getBalance(uuid) - Double.parseDouble(String.valueOf(args[1]));
                if (money < 0) {
                    return true;
                }
                plugin.balances.setBalance(uuid, money);

            } catch (Exception e) {}
        }
        return true;
    }
}
