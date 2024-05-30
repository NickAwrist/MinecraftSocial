package nicholas.minecraftsocial;

import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SocialUser {

    private static HashMap<UUID, SocialUser> socialUsers = new HashMap<>();
    private static DatabaseConnection databaseConnection = MinecraftSocial.getDatabaseConnection();

    private final Player player;
    private String username;
    private final UUID uuid;
    private final ArrayList<UUID> friendsList;

    public SocialUser(Player player) {
        this.player = player;
        this.username = player.getName();
        this.uuid = player.getUniqueId();
        this.friendsList = new ArrayList<>();

        addSocialUser(this);
    }

    public SocialUser(Player player, String username, ArrayList<UUID> friendsList) {
        this.player = player;
        this.username = player.getName();
        this.uuid = player.getUniqueId();
        this.friendsList = friendsList;

        if(this.username == null) {
            this.username = username;
        }

        addSocialUser(this);
    }

// ---------------------------------------------------- STATIC METHODS
    // Get the SocialUser object for a certain player by their UUID
    public static SocialUser createSocialUser(Player player) throws SQLException {
        if(socialUsers.containsKey(player.getUniqueId())) {
            return socialUsers.get(player.getUniqueId());
        }else{
            return databaseConnection.createSocialUser(player);
        }
    }

    // Add a SocialUser to the static HashMap
    public static void addSocialUser(SocialUser user) {
        socialUsers.put(user.getUuid(), user);
    }

    // Remove a SocialUser from the static HashMap
    public static void removeSocialUser(Player player) {
        socialUsers.remove(player.getUniqueId());
    }
// ---------------------------------------------------- INSTANCE METHODS

    // Getters and Setters
    public Player getPlayer() {
        return player;
    }
    public UUID getUuid() {
        return uuid;
    }
    public String getUsername() {
        return username;
    }

    // Get the friends list
    public ArrayList<UUID> getFriendsList() {
        return friendsList;
    }

    // Add and remove friends. Update database for storage
    public void addFriend(SocialUser friend) throws SQLException {
        friendsList.add(friend.getUuid());
        MinecraftSocial.getDatabaseConnection().updateFriendList(this, friend, true);
    }
    public void removeFriend(SocialUser friend) throws SQLException {
        friendsList.remove(friend.getUuid());
        MinecraftSocial.getDatabaseConnection().updateFriendList(this, friend, false);
    }

}
