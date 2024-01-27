package me.thegoldenmine.com.hamstercoin;

import me.thegoldenmine.com.hamstercoin.Commands.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;

public final class HamsterCoin extends JavaPlugin {
    private Balances balances;
    public String version;
    public HamsterCoin hamsterCoin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Objects.requireNonNull(getCommand("paycoin")).setExecutor(new PayCommand(this));
        Objects.requireNonNull(getCommand("resetcoin")).setExecutor(new ResetCommand(this));
        Objects.requireNonNull(getCommand("setcoin")).setExecutor(new SetCommand(this));
        Objects.requireNonNull(getCommand("givecoin")).setExecutor(new GiveCommand(this));
        Objects.requireNonNull(getCommand("uuid")).setExecutor(new UuidCommand());
        Objects.requireNonNull(getCommand("hamsterbal")).setExecutor(new BalCommand(this));
        try {
            balances = new Balances(this);
        } catch (IOException e) {
            throw new RuntimeException("Cannot prepare Balances data", e);
        }
    }

    @Override
    public void onDisable() {
    }

    public Balances getBalances() {
        return balances;
    }

    private boolean setupManager(){
        version = "N/A";
        try{
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        }catch (ArrayIndexOutOfBoundsException e){
            return false;
        }
        switch (version) {
            case "v1_16_R1":
            case "v1_17_R1":
            case "v1_16_R3":
            case "v1_16_R2":
                hamsterCoin = new HamsterCoin();
                break;
        }
        return hamsterCoin != null;
    }
}
