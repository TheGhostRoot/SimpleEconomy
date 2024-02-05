package me.thegoldenmine.com.hamstercoin;

import me.thegoldenmine.com.hamstercoin.databases.DatabaseManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

public class Balances {
    public HexCoin plugin;
    private File dataFile;
    private FileConfiguration data;

    public Connection mysql_connection = null;

    private String messages_table = "eco_messages";
    private String balances_table = "eco_balance";

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
        reload();
        if (!dataFile.exists() && dataFile.createNewFile()) {
            setMessages();
            plugin.getLogger().info("Made the file for economy");

        } else if (getDBisActive()) {
                try {
                    mysql_connection = DriverManager.getConnection(getDBhost(), getDBusername(), getDBpassword());
                    DatabaseManager.setConnection(mysql_connection);

                    plugin.getLogger().info("Connected to database");

                    List<String> message_table = new ArrayList<>();
                    message_table.add("message TEXT NOT NULL, ");
                    message_table.add("used_for TEXT NOT NULL");

                    List<String> player_balance = new ArrayList<>();
                    player_balance.add("player_uuid TEXT NOT NULL, ");
                    player_balance.add("money DOUBLE NOT NULL");

                    DatabaseManager.createTableSQL(messages_table, message_table);
                    DatabaseManager.createTableSQL(balances_table, player_balance);

                    Map<String, List<Object>> res = DatabaseManager.getDataSQL(messages_table, "*",
                            "", null, null, "", 0);

                    if (res.get("message").isEmpty() && res.get("used_for").isEmpty()) {
                        // set default
                        /*
                        data.set("got_payed", "You have been payed /money/ from /player/");
        data.set("not_enough_money", "You don't have enough money");
        data.set("send_payment", "You have payed /money/ to /player/");
        data.set("reset_balance", "You have rested the /player/ balance to 0");
        data.set("set_bal", "You have set the balance of /player/ to /money/");
        data.set("balance", "Your balance is /money/");
        data.set("console_name", "CONSOLE");
        data.set("error_give_yourself", "You can't give yourself money");
        data.set("error_missing_permission", "You don't have /permissions/");
        data.set("error_missing_argument", "Missing /arg/");
                         */
                        Map<String, String> data = new HashMap<>();
                        data.put("got_payed", "You have been payed /money/ from /player/");
                        data.put("not_enough_money", "You don't have enough money");
                        data.put("send_payment", "You have payed /money/ to /player/");
                        data.put("reset_balance", "You have rested the /player/ balance to 0");
                        data.put("set_bal", "You have set the balance of /player/ to /money/");
                        data.put("balance", "Your balance is /money/");
                        data.put("console_name", "CONSOLE");
                        data.put("error_give_yourself", "You can't give yourself money");
                        data.put("error_missing_permission", "You don't have /permissions/");
                        data.put("error_missing_argument", "Missing /arg/");

                        for (Map.Entry<String, String> entry : data.entrySet()) {
                            List<Object> d = new ArrayList<>();
                            d.add(entry.getValue());
                            d.add(entry.getKey());

                            DatabaseManager.addDataSQL(messages_table, "message, used_for", "?, ?", d);
                        }
                    }

                } catch (Exception e) {
                    plugin.getLogger().info("Can't connect to DB");
                }
        }
    }


    public void shutDown() {
        try {
            if (mysql_connection != null) {
                mysql_connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private String getMessageSQL(String path, String defaultValue, HashMap<String, String> args) {
        List<Object> condition = new ArrayList<>();
        condition.add(path);

        Map<String, List<Object>> res = DatabaseManager.getDataSQL(messages_table, "message",
                "used_for = ?", condition, null, "", 0);
        if (res == null || res.get("message").isEmpty()) {
            return defaultValue;
        }

        return plugin.translateColors(String.valueOf(res.get("message").get(0)), args);
    }

    public String getYouGotPayedMessage(double money, String senderName) {
        HashMap<String, String> args = new HashMap<>();
        args.put("money", String.valueOf(money));
        args.put("player", senderName);
        if (mysql_connection != null) {
            return getMessageSQL("got_payed", "You have been payed " + money + " from " + senderName, args);

        } else {
            Object m = data.get("got_payed");
            return m == null ? "You have been payed " + money + " from " + senderName : plugin.translateColors(String.valueOf(m), args);
        }
    }

    public String getMissingArgumentMessage(String arguments) {
        HashMap<String, String> args = new HashMap<>();
        args.put("arg", arguments);
        if (mysql_connection != null) {
            return getMessageSQL("error_missing_argument", "Missing " + arguments, args);

        } else {
            Object m = data.get("error_missing_argument");
            return m == null ? "Missing " + arguments : plugin.translateColors(String.valueOf(m), args);
        }
    }

    public boolean getDBisActive() {
        return data.getBoolean("db.enable");
    }

    public String getDBusername() {
        return data.getString("db.username");
    }

    public String getDBpassword() {
        return data.getString("db.password");
    }

    public String getDBhost() {
        return data.getString("db.host");
    }


    public String getYouPayedMessage(double money, String resiveName) {
        HashMap<String, String> args = new HashMap<>();
        args.put("money", String.valueOf(money));
        args.put("player", resiveName);
        if (mysql_connection != null) {
            return getMessageSQL("send_payment", "You have payed " + money + " to " + resiveName, args);

        } else {
            Object m = data.get("send_payment");
            return m == null ? "You have payed " + money + " to " + resiveName : plugin.translateColors(String.valueOf(m), args);
        }
    }

    public String getYourBalanceMessage(double money) {
        HashMap<String, String> args = new HashMap<>();
        args.put("money", String.valueOf(money));
        if (mysql_connection != null) {
            return getMessageSQL("balance", "Your balance is " + money, args);

        } else {
            Object m = data.get("balance");
            return m == null ? "Your balance is " + money : plugin.translateColors(String.valueOf(m), args);
        }
    }

    public String getMissingPermissionsMessage(String permissions) {
        HashMap<String, String> args = new HashMap<>();
        args.put("permissions", permissions);
        if (mysql_connection != null) {
            return getMessageSQL("error_missing_permission", "You don't have " + permissions, args);

        } else {
            Object m = data.get("error_missing_permission");
            return m == null ? "You don't have " + permissions : plugin.translateColors(String.valueOf(m), args);
        }
    }

    public String getCantGiveYourselfMessage() {
        if (mysql_connection != null) {
            return getMessageSQL("error_give_yourself", "You can't give yourself money", null);

        } else {
            Object m = data.get("error_give_yourself");
            return m == null ? "You can't give yourself money" : plugin.translateColors(String.valueOf(m), null);
        }
    }

    public String getNotEnoughMoneyMessage() {
        if (mysql_connection != null) {
            return getMessageSQL("not_enough_money", "You don't have enough money", null);

        } else {
            Object m = data.get("not_enough_money");
            return m == null ? "You don't have enough money" : plugin.translateColors(String.valueOf(m), null);
        }

    }

    public String getResetBalMessage(String resetPlayerName) {
        HashMap<String, String> map = new HashMap<>();
        map.put("player", resetPlayerName);
        if (mysql_connection != null) {
            return getMessageSQL("reset_balance", "Your money were set to 0", map);

        } else {
            Object m = data.get("reset_balance");
            return m == null ? "Your money were set to 0" : plugin.translateColors(String.valueOf(m), map);
        }
    }

    public String getSetBalMessage(String playerName, double money) {
        HashMap<String, String> map = new HashMap<>();
        map.put("money", String.valueOf(money));
        map.put("player", playerName);
        if (mysql_connection != null) {
            return getMessageSQL("set_bal", "You have set the balance of " + playerName + " to " + money, map);

        } else {
            Object m = data.get("set_bal");
            return m == null ? "You have set the balance of " + playerName + " to " + money : plugin.translateColors(String.valueOf(m), map);
        }
    }

    public String getConsoleName() {
        if (mysql_connection != null) {
            return getMessageSQL("console_name", "CONSOLE", null);

        } else {
            Object m = data.get("console_name");
            return m == null ? "CONSOLE" : plugin.translateColors(String.valueOf(m), null);
        }
    }

    public void setMessages() {
        data.set("got_payed", "You have been payed /money/ from /player/");
        data.set("not_enough_money", "You don't have enough money");
        data.set("send_payment", "You have payed /money/ to /player/");
        data.set("reset_balance", "You have rested the /player/ balance to 0");
        data.set("set_bal", "You have set the balance of /player/ to /money/");
        data.set("balance", "Your balance is /money/");
        data.set("console_name", "CONSOLE");
        data.set("error_give_yourself", "You can't give yourself money");
        data.set("error_missing_permission", "You don't have /permissions/");
        data.set("error_missing_argument", "Missing /arg/");
        data.set("db.enable", false);
        data.set("db.username", "root");
        data.set("db.password", "123");
        data.set("db.host", "jdbc:mysql://localhost:3306/jcorechat");
        save();
        reload();
    }


    synchronized public void give(UUID payee, double money) {
        setBalance(payee, getBalance(payee) + money);
        save();
        reload();
    }

    synchronized public double getBalance(UUID playerUuid) {
        if (mysql_connection != null) {
            List<Object> condition = new ArrayList<>();
            condition.add(playerUuid.toString());

            Map<String, List<Object>> res = DatabaseManager.getDataSQL(balances_table, "money",
                    "player_uuid = ?", condition, null, "", 0);

            if (res == null || res.get("money").isEmpty()) {
                setBalance(playerUuid, 0.0);
                return 0;
            }

            try {
                return Double.parseDouble(String.valueOf(res.get("money").get(0)));

            } catch (Exception e) {
                setBalance(playerUuid, 0.0);
                return 0;
            }

        } else {
            Double balance = (Double) data.get("balance_" + playerUuid);
            if (balance == null) {
                setBalance(playerUuid, 0.0);
                save();
                return 0;
            }

            return balance;
        }
    }

    synchronized public void setBalance(UUID playerUuid, double amount) {
        if (mysql_connection != null) {
            List<Object> data = new ArrayList<>();
            data.add(playerUuid.toString());

            Map<String, List<Object>> res = DatabaseManager.getDataSQL(balances_table, "player_uuid",
                    "player_uuid = ?", data, null, "", 0);


            if (res == null || res.get("player_uuid").isEmpty()) {
                // no money
                data.add(amount);
                DatabaseManager.addDataSQL(balances_table, "player_uuid, money", "?, ?", data);

            } else {
                // has money
                List<Object> set = new ArrayList<>();
                set.add(amount);

                DatabaseManager.editDataSQL(balances_table, "money = ?", set,
                        "player_uuid = ?", data);
            }

        } else {
            data.set("balance_" + playerUuid, amount);
            save();
            reload();
        }
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
