package nicholas.minecraftsocial;

import nicholas.minecraftsocial.commands.Friend;
import nicholas.minecraftsocial.commands.FriendTabCompleter;
import nicholas.minecraftsocial.commands.Profile;
import nicholas.minecraftsocial.commands.debugFriendList;
import nicholas.minecraftsocial.database.DatabaseConnection;
import nicholas.minecraftsocial.database.JSON_DB;
import nicholas.minecraftsocial.database.MySQL_DB;
import nicholas.minecraftsocial.events.ClickPlayerEvent;
import nicholas.minecraftsocial.events.GUIInventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import nicholas.minecraftsocial.events.LoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

public final class MinecraftSocial extends JavaPlugin {

    private static DatabaseConnection databaseConnection;
    private static MinecraftSocial plugin;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();

        /*
                TODO
        *        - Create more robust error message when database fails to connect.
        *        - If the database fails to connect, create JSON storage alternative
         */

        if(getConfig().getBoolean("DatabaseType.mysql.enabled")) {
            databaseConnection = new MySQL_DB();
        }else{
            databaseConnection = new JSON_DB();
        }

        databaseConnection.connect();
        SocialUser.setDatabaseConnection(databaseConnection);
        scheduleUpdateTask();

        // Register LoginEvent
        getServer().getPluginManager().registerEvents(new LoginEvent(), this);
        getServer().getPluginManager().registerEvents(new ClickPlayerEvent(), this);
        getServer().getPluginManager().registerEvents(new GUIInventoryClickEvent(), this);

        // Register /friend command
        this.getCommand("friend").setExecutor(new Friend());
        this.getCommand("friend").setTabCompleter(new FriendTabCompleter());
        this.getCommand("profile").setExecutor(new Profile());

        //this.getCommand("debugFriendList").setExecutor(new debugFriendList());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        try {

            if(databaseConnection.needsUpdate()){
                databaseConnection.updateDatabase();
            }

            databaseConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MinecraftSocial getPlugin() {
        return plugin;
    }

    private void scheduleUpdateTask(){
        new BukkitRunnable() {
            @Override
            public void run() {
                if(databaseConnection.needsUpdate()){
                    databaseConnection.updateDatabase();
                }
            }
        }.runTaskTimer(plugin, 0, 20*5);
    }
}
