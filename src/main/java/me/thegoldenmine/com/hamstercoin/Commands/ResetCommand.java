package me.thegoldenmine.com.hamstercoin.Commands;

import me.thegoldenmine.com.hamstercoin.HexCoin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCommand implements CommandExecutor {

    private final HexCoin plugin;

    public ResetCommand(HexCoin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            // /resethex <playername>
            if (!player.hasPermission("hex.reset")) {
                player.sendMessage(plugin.balances.getMissingPermissionsMessage("hex.reset"));
                return true;
            }

            if (args.length < 1) {
                player.sendMessage(plugin.balances.getMissingArgumentMessage("<player name>"));
                return true;
            }

            Player resetPlayer = Bukkit.getPlayer(args[0]);
            if (resetPlayer == null) {
                return true;
            }

            plugin.balances.setBalance(resetPlayer.getUniqueId(), 0);
            player.sendMessage(plugin.balances.getResetBalMessage(resetPlayer.getDisplayName()));
        }
        return true;
    }
}
