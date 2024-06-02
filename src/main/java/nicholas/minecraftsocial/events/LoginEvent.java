package nicholas.minecraftsocial.events;

import nicholas.minecraftsocial.Messenger;
import nicholas.minecraftsocial.SocialUser;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.UUID;

public class LoginEvent implements Listener {

    @EventHandler
    public void onLogin(PlayerJoinEvent e) throws SQLException {

        // Creates a SocialUser instance for the player. If the player is new, it will create a new instance and add
        // it to the database
        SocialUser user = SocialUser.getSocialUser(e.getPlayer().getUniqueId());

        // Get the number of friends online
        SocialUser friend;
        int onlineFriends = 0;
        for(UUID uuid : user.getFriendsList()){
            friend = SocialUser.getSocialUserFromList(uuid);
            if(friend != null && friend.getPlayer().isOnline()){
                onlineFriends++;
            }
        }

        Messenger.sendInfo(e.getPlayer(), "You have " + onlineFriends + " friends online.");
    }

}