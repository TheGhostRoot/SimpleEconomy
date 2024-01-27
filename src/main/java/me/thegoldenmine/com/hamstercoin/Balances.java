package me.thegoldenmine.com.hamstercoin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Balances {
    public HamsterCoin plugin;
    private final File dataFile;
    private FileConfiguration data;

    public Balances(HamsterCoin plugin) throws IOException {
        /*dataFile = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("HamsterCoin")).getDataFolder(), "config.yml");
        data = YamlConfiguration.loadConfiguration(dataFile);*/
        this.plugin = plugin;

        File dataFolder = plugin.getDataFolder();
        dataFolder.mkdir();
        dataFile = new File(dataFolder, "balance.yml");
        dataFile.createNewFile();
        reload();
    }

    synchronized public void pay(UUID payer, UUID payee, int money) throws IOException {
        Player player = Bukkit.getPlayer(payer);
        Player player1 = Bukkit.getPlayer(payee);
        int payerBalance = getBalance(payer);
        int payeeBalance = getBalance(payee);
        int payerNewBalance = payerBalance - money;
        String moneyStr = String.valueOf(money);
        if (payerNewBalance >= 0) {
            if (!payee.equals(payer)) {
                int payeeNewBalance = payeeBalance + money;
                setBalance(payer, payerNewBalance);
                player.sendMessage(ChatColor.GREEN+"You have payed "+ChatColor.GOLD+moneyStr+ChatColor.GREEN+" money to "+ChatColor.GOLD+player1.getName()+ChatColor.GREEN+" player!");
                setBalance(payee, payeeNewBalance);
                player1.sendMessage(ChatColor.GREEN+"You have been payed "+ChatColor.GOLD+moneyStr+ChatColor.GREEN+" money from "+ChatColor.GOLD+player.getName()+ChatColor.GREEN+" player!");
                save();
                player.sendMessage(ChatColor.GREEN+"Your balance is successfully save in the config file.");
                player1.sendMessage(ChatColor.GREEN+"Your balance is successfully save in the config file.");
            }else{

                if (player != null) {
                    player.sendMessage(ChatColor.RED+""+ChatColor.ITALIC+"You can't give yourself "+ChatColor.GOLD+""+ChatColor.ITALIC+moneyStr+ChatColor.RED+""+ChatColor.ITALIC+" money!");
                }
            }
        }
    }

    synchronized public void give(UUID payee, int money) {
        int payeeBalance = getBalance(payee);
        int payeeNewBalance = payeeBalance + money;
        setBalance(payee, payeeNewBalance);
        save();
    }

    synchronized public int getBalance(UUID playerUuid) {
        Integer balance = (Integer) data.get("balance[.=" + playerUuid + "]");
        if (balance == null) {
            setBalance(playerUuid, 0);
            save();
            return 0;
        }

        return balance;
    }

    synchronized public void setBalance(UUID playerUuid, int amount) {
        data.set("balance[.=" + playerUuid + "]", amount);
    }

    synchronized public void save() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            throw new RuntimeException("Cannot save balances", e);
        }
    }

    synchronized public void reload() {
        data = YamlConfiguration.loadConfiguration(dataFile);
    }
}
