package me.thegoldenmine.com.hamstercoin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Balances {
    public HexCoin plugin;
    private File dataFile;
    private FileConfiguration data;

    public Balances(HexCoin plugin) throws IOException {
        /*dataFile = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("HexCoin")).getDataFolder(), "config.yml");
        data = YamlConfiguration.loadConfiguration(dataFile);*/
        this.plugin = plugin;

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            if (dataFolder.mkdir()) {
                plugin.getLogger().info("Made folder for economy");

            } else {
                plugin.getLogger().info("Can't make folder for economy");
                return;
            }
        }
        dataFile = new File(dataFolder, "balance.yml");
        if (!dataFile.exists() && dataFile.createNewFile()) {
            setMessages();
            plugin.getLogger().info("Made the file for economy");
        }
        reload();
    }

    synchronized public void pay(Player payer, Player payee, double money) {
        UUID payeeUUID = payee.getUniqueId();
        UUID payerUUID = payer.getUniqueId();
        double payerBalance = getBalance(payerUUID);
        if (payerBalance < money) {
            payer.sendMessage(getNotEnoughMoneyMessage());
            return;
        }
        double payerNewBalance = payerBalance - money;
        if (payerNewBalance >= 0) {
            if (!payee.equals(payer)) {
                setBalance(payerUUID, payerNewBalance);
                setBalance(payeeUUID, getBalance(payeeUUID) + money);
                payer.sendMessage(getYouPayedMessage(money, payee.getDisplayName()));
                payee.sendMessage(getYouGotPayedMessage(money, payer.getDisplayName()));
                save();
                reload();

            } else {
                payer.sendMessage(getCantGiveYourselfMessage());
            }
        }
    }

    public String getYouGotPayedMessage(double money, String senderName) {
        Object m = data.get("got_payed");
        HashMap<String, String> args = new HashMap<>();
        args.put("money", String.valueOf(money));
        args.put("sender", senderName);
        return m == null ? "You have been payed " + money + " from " + senderName : plugin.translateColors(String.valueOf(m), args);
    }

    public String getMissingArgumentMessage(String arguments) {
        Object m = data.get("error_missing_argument");
        HashMap<String, String> args = new HashMap<>();
        args.put("arg", arguments);
        return m == null ? "Missing " + arguments : plugin.translateColors(String.valueOf(m), args);
    }

    public String getYouPayedMessage(double money, String resiveName) {
        Object m = data.get("send_payment");
        HashMap<String, String> args = new HashMap<>();
        args.put("money", String.valueOf(money));
        args.put("sender", resiveName);
        return m == null ? "You have payed " + money + " to " + resiveName : plugin.translateColors(String.valueOf(m), args);
    }

    public String getYourBalanceMessage(double money) {
        Object m = data.get("balance");
        HashMap<String, String> args = new HashMap<>();
        args.put("money", String.valueOf(money));
        return m == null ? "Your balance is " + money : plugin.translateColors(String.valueOf(m), args);
    }

    public String getMissingPermissionsMessage(String permissions) {
        Object m = data.get("error_missing_permission");
        HashMap<String, String> args = new HashMap<>();
        args.put("permissions", permissions);
        return m == null ? "You don't have " + permissions : plugin.translateColors(String.valueOf(m), args);
    }

    public String getCantGiveYourselfMessage() {
        Object m = data.get("error_give_yourself");
        return m == null ? "You can't give yourself money": plugin.translateColors(String.valueOf(m), null);
    }

    public String getNotEnoughMoneyMessage() {
        Object m = data.get("not_enough_money");
        return m == null ? "You don't have enough money": plugin.translateColors(String.valueOf(m), null);
    }

    public String getResetBalMessage(String resetPlayerName) {
        Object m = data.get("reset_balance");
        HashMap<String, String> map = new HashMap<>();
        map.put("player", resetPlayerName);
        return m == null ? "You don't have enough money": plugin.translateColors(String.valueOf(m), map);
    }

    public String getSetBalMessage(String playerName, double money) {
        Object m = data.get("set_bal");
        HashMap<String, String> map = new HashMap<>();
        map.put("money", String.valueOf(money));
        map.put("player", playerName);
        return m == null ? "You have set the balance of /player/ to /money/" : plugin.translateColors(String.valueOf(m), map);
    }

    public String getConsoleName() {
        Object m = data.get("console_name");
        return m == null ? "CONSOLE": plugin.translateColors(String.valueOf(m), null);
    }

    public void setMessages() {
        data.set("got_payed", "You have been payed /money/ from /player/");
        data.set("not_enough_money", "You don't have enough money");
        data.set("send_payment", "You have payed /money/ to /player/");
        data.set("send_payment", "You have payed /money/ to /player/");
        data.set("reset_balance", "You have rested the /player/ balance to 0");
        data.set("set_bal", "You have set the balance of /player/ to /money/");
        data.set("balance", "Your balance is /money/");
        data.set("console_name", "CONSOLE");
        data.set("error_give_yourself", "You can't give yourself money");
        data.set("error_missing_permission", "You don't have /permissions/");
        data.set("error_missing_argument", "Missing /arg/");
        save();
        reload();
    }


    synchronized public void give(UUID payee, double money) {
        setBalance(payee, getBalance(payee) + money);
        save();
        reload();
    }

    synchronized public double getBalance(UUID playerUuid) {
        Double balance = (Double) data.get("balance_" + playerUuid);
        if (balance == null) {
            setBalance(playerUuid, 0.0);
            save();
            return 0;
        }

        return balance;
    }

    synchronized public void setBalance(UUID playerUuid, double amount) {
        data.set("balance_" + playerUuid, amount);
        save();
        reload();
    }

    synchronized public void save() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            throw new RuntimeException("Can't save balances", e);
        }
    }

    synchronized public void reload() {
        data = YamlConfiguration.loadConfiguration(dataFile);
    }
}
