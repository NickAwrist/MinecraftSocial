package nicholas.minecraftsocial;

import nicholas.minecraftsocial.database.DatabaseConnection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SocialUser {

    private static final HashMap<UUID, SocialUser> socialUsers = new HashMap<>();
    private static DatabaseConnection databaseConnection;

    private transient Player player;
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
    public static SocialUser getSocialUser(UUID uuid){

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

    public static ArrayList<SocialUser> usersAsList() {
        ArrayList<SocialUser> users = new ArrayList<>();
        for (Map.Entry<UUID, SocialUser> entry : socialUsers.entrySet()) {
            users.add(entry.getValue());
        }
        return users;
    }

    public static void setDatabaseConnection(DatabaseConnection connection) {
        databaseConnection = connection;
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
    public void addIncomingRequest(SocialUser sender){
        incomingRequests.add(sender.getUuid());
        databaseConnection.setUpdatePending(true);
    }
    public void addOutgoingRequest(SocialUser target){
        outgoingRequests.add(target.getUuid());
        databaseConnection.setUpdatePending(true);
    }

    public void removeIncomingRequest(SocialUser sender){
        incomingRequests.remove(sender.getUuid());
        databaseConnection.setUpdatePending(true);
    }
    public void removeOutgoingRequest(SocialUser target){
        outgoingRequests.remove(target.getUuid());
        databaseConnection.setUpdatePending(true);
    }

    // Add and remove friends. Update database for storage
    public void addFriend(SocialUser target){
        friendsList.add(target.getUuid());
        databaseConnection.setUpdatePending(true);
    }
    public void removeFriend(SocialUser target){
        friendsList.remove(target.getUuid());
        databaseConnection.setUpdatePending(true);
    }

}
