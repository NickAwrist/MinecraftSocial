package nicholas.minecraftsocial;

import nicholas.minecraftsocial.commands.Friend;
import nicholas.minecraftsocial.commands.FriendTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import nicholas.minecraftsocial.events.LoginEvent;

public final class MinecraftSocial extends JavaPlugin {

    private static DatabaseConnection databaseConnection = new DatabaseConnection();

    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            databaseConnection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(new LoginEvent(), this);

        // register commands
        this.getCommand("friend").setExecutor(new Friend());
        this.getCommand("friend").setTabCompleter(new FriendTabCompleter());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        try {
            databaseConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getDatabaseConnection(){
        return databaseConnection;
    }
}
