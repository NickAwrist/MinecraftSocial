package nicholas.minecraftsocial;

import org.bukkit.plugin.java.JavaPlugin;

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
