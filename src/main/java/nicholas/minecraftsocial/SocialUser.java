package nicholas.minecraftsocial;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SocialUser {

    private static final HashMap<UUID, SocialUser> socialUsers = new HashMap<>();
    private static final DatabaseConnection databaseConnection = MinecraftSocial.getDatabaseConnection();

    private Player player;
    private String username;
    private final UUID uuid;
    private final ArrayList<UUID> friendsList;
    private final ArrayList<UUID> incomingRequests;
    private final ArrayList<UUID> outgoingRequests;

    public SocialUser(Player player) {
        this.player = player;
        this.username = player.getName();
        this.uuid = player.getUniqueId();
        this.friendsList = new ArrayList<>();
        this.incomingRequests = new ArrayList<>();
        this.outgoingRequests = new ArrayList<>();

        addSocialUser(this);
    }

    public SocialUser(Player player, ArrayList<UUID> friendsList, ArrayList<UUID> incomingRequests, ArrayList<UUID> outgoingRequests) {
        this.player = player;
        this.username = player.getName();
        this.uuid = player.getUniqueId();
        this.friendsList = friendsList;
        this.incomingRequests = incomingRequests;
        this.outgoingRequests = outgoingRequests;

        addSocialUser(this);
    }

// ---------------------------------------------------- STATIC METHODS
    // Get the SocialUser object for a certain player by their UUID
    public static SocialUser getSocialUser(UUID uuid) throws SQLException {

        if(socialUsers.containsKey(uuid)) {
            SocialUser user = socialUsers.get(uuid);
            user.updatePlayerInstance();

            socialUsers.put(uuid, user);

            return user;
        }else{
            return databaseConnection.getSocialUser(uuid);
        }
    }

    public static SocialUser getSocialUserFromList(UUID uuid) {
        return socialUsers.get(uuid);
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
    public void updatePlayerInstance(){
        this.player = Bukkit.getPlayer(uuid);
    }
    public UUID getUuid() {
        return uuid;
    }
    public String getUsername() {
        return username;
    }
    public ArrayList<UUID> getFriendsList() {
        return friendsList;
    }
    public ArrayList<UUID> getIncomingRequests() {
        return incomingRequests;
    }
    public ArrayList<UUID> getOutgoingRequests() {
        return outgoingRequests;
    }

    // Add and remove friend requests. Update database for storage
    public void addIncomingRequest(SocialUser sender) throws SQLException {
        incomingRequests.add(sender.getUuid());
        MinecraftSocial.getDatabaseConnection().updateFriendList(this);
    }
    public void addOutgoingRequest(SocialUser target) throws SQLException {
        outgoingRequests.add(target.getUuid());
        MinecraftSocial.getDatabaseConnection().updateFriendList(this);
    }

    public void removeIncomingRequest(SocialUser sender) throws SQLException {
        incomingRequests.remove(sender.getUuid());
        MinecraftSocial.getDatabaseConnection().updateFriendList(this);
    }
    public void removeOutgoingRequest(SocialUser target) throws SQLException {
        outgoingRequests.remove(target.getUuid());
        MinecraftSocial.getDatabaseConnection().updateFriendList(this);
    }

    // Add and remove friends. Update database for storage
    public void addFriend(SocialUser target) throws SQLException {
        friendsList.add(target.getUuid());
        MinecraftSocial.getDatabaseConnection().updateFriendList(this);
    }
    public void removeFriend(SocialUser target) throws SQLException {
        friendsList.remove(target.getUuid());
        MinecraftSocial.getDatabaseConnection().updateFriendList(this);
    }

}
