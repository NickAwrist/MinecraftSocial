package nicholas.minecraftsocial;

import nicholas.minecraftsocial.commands.Friend;
import nicholas.minecraftsocial.commands.FriendTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import nicholas.minecraftsocial.events.LoginEvent;

public final class MinecraftSocial extends JavaPlugin {

    private static final DatabaseConnection databaseConnection = new DatabaseConnection();
    private static MinecraftSocial plugin;

    @Override
    public void onEnable() {
        plugin = this;
        // Plugin startup logic
        // Connect to database
        /*
                TODO
        *        - Create more robust error message when database fails to connect.
        *        - If the database fails to connect, create JSON storage alternative
         */
        try {
            databaseConnection.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Register LoginEvent
        getServer().getPluginManager().registerEvents(new LoginEvent(), this);

        // Register /friend command
        this.getCommand("friend").setExecutor(new Friend());
        this.getCommand("friend").setTabCompleter(new FriendTabCompleter());


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        // Disconnect from the database
        /*
                TODO
         *       - Create more robust error message
         */
        try {
            databaseConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getDatabaseConnection(){
        return databaseConnection;
    }

    public static MinecraftSocial getPlugin() {
        return plugin;
    }
}
