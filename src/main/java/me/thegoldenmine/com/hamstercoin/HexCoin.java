package me.thegoldenmine.com.hamstercoin;

import me.thegoldenmine.com.hamstercoin.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HexCoin extends JavaPlugin {
    public Balances balances;
    public String version;
    public HexCoin hexCoin;

    @Override
    public void onEnable() {
        version = "N/A";
        try{
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        } catch (ArrayIndexOutOfBoundsException e){
            getLogger().severe("Failed to setup HexCoin");
            getLogger().severe("Your server version is not compatible with this plugin!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (version.equals("N/A")) {
            disablePlugin();
            return;
        }

        switch (version) {
            case "v1_18_R1":
            case "v1_18_R2":
            case "v1_19_R1":
            case "v1_16_R3":
            case "v1_17_R1":
            case "v1_19_R2":
            case "v1_19_R3":
            case "v1_20_R1":
            case "v1_20_R2":
            case "v1_20_R3":
                hexCoin = this;
                break;
        }

        if (hexCoin == null) {
            disablePlugin();
            return;
        }

        try {
            balances = new Balances(this);

        } catch (IOException e) {
            getLogger().severe("Failed to read local balance");
            disablePlugin();
            return;
        }

        getCommand("payhex").setExecutor(new PayCommand(this));
        getCommand("resethex").setExecutor(new ResetCommand(this));
        getCommand("sethex").setExecutor(new SetCommand(this));
        getCommand("givehex").setExecutor(new GiveCommand(this));
        getCommand("balhex").setExecutor(new BalCommand(this));
        getCommand("reloadhex").setExecutor(new ReloadHexCommand(this));
        getCommand("takehex").setExecutor(new TakeCommand(this));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            balances.shutDown();
        }, "Shutdown-thread"));

        getLogger().info("Plugin Loaded!");
    }

    public String translateColors(String msg, HashMap<String, String> args) {
        if (args != null && !args.isEmpty()) {
            for (String cust_placeholder : msg.split("/").clone()) {
                if (args.containsKey(cust_placeholder)) {
                    msg = msg.replace("/"+cust_placeholder+ "/", args.get(cust_placeholder));
                }
            }
        }
        Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})"); // &(#[A-Fa-f0-9]{6})
        char COLOR_CHAR = ChatColor.COLOR_CHAR;
        Matcher matcher = HEX_PATTERN.matcher(msg);
        StringBuffer buffer = new StringBuffer(msg.length() + 32);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, COLOR_CHAR + "x"
                    + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
                    + COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
                    + COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5)
            );
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    private void disablePlugin() {
        getLogger().severe("Failed to setup HexCoin");
        getLogger().severe("Your server version is not compatible with this plugin!");
        Bukkit.getPluginManager().disablePlugin(this);
    }

    @Override
    public void onDisable() {
    }

}
