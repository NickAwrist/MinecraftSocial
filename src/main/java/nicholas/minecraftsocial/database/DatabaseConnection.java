package nicholas.minecraftsocial.database;

import com.google.gson.Gson;
import nicholas.minecraftsocial.MinecraftSocial;
import nicholas.minecraftsocial.SocialUser;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public abstract interface DatabaseConnection {

    Gson gson = new Gson();
    Plugin plugin = MinecraftSocial.getPlugin();

    void connect();
    void disconnect();

    boolean needsUpdate();
    SocialUser getSocialUser(UUID uuid);
    void addUser(SocialUser user);
    void removeUser(SocialUser user);
    void updateDatabase();
    void setUpdatePending(boolean updating);

}
