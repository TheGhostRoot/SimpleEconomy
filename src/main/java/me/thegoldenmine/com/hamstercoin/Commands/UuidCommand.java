package me.thegoldenmine.com.hamstercoin.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UuidCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player)sender;
            player.sendMessage(ChatColor.GREEN+"Your UUID is "+ChatColor.GOLD+""+player.getUniqueId());
        }else{
            sender.sendMessage("Only players can use this command!");
        }
        return true;
    }
}
