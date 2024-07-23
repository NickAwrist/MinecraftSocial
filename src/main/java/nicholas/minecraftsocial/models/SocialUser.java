package nicholas.minecraftsocial.models;

import nicholas.minecraftsocial.database.DatabaseConnection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SocialUser {

    private static final HashMap<UUID, SocialUser> socialUsers = new HashMap<>();
    private static DatabaseConnection databaseConnection;

    private transient SocialPlayer player;
    private String username;
    private final UUID uuid;
    private final ArrayList<UUID> friendsList;
    private final ArrayList<UUID> incomingRequests;
    private final ArrayList<UUID> outgoingRequests;
    private final String dateFirstJoined;

    public SocialUser(SocialPlayer player) {
        this.player = player;
        this.username = player.getUsername();
        this.uuid = player.getUUID();
        this.friendsList = new ArrayList<>();
        this.incomingRequests = new ArrayList<>();
        this.outgoingRequests = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        this.dateFirstJoined = currentDate.format(formatter);

        addSocialUser(this);
    }

    public SocialUser(SocialPlayer player, ArrayList<UUID> friendsList, ArrayList<UUID> incomingRequests, ArrayList<UUID> outgoingRequests, String dateFirstJoined) {
        this.player = player;
        this.username = player.getUsername();
        this.uuid = player.getUUID();
        this.friendsList = friendsList;
        this.incomingRequests = incomingRequests;
        this.outgoingRequests = outgoingRequests;
        this.dateFirstJoined = dateFirstJoined;

        addSocialUser(this);
    }

// ---------------------------------------------------- STATIC METHODS
    // Get the SocialUser object for a certain player by their UUID
    public static SocialUser getSocialUser(UUID uuid){

        SocialUser user;
        if(socialUsers.containsKey(uuid)) {
            user = socialUsers.get(uuid);
            user.updatePlayerInstance();
        }else {
            user = databaseConnection.getSocialUser(uuid);
        }

        if(user == null){
            return null;
        }

        addSocialUser(user);
        return user;
    }

    public static SocialUser getSocialUserFromList(UUID uuid) {
        return socialUsers.get(uuid);
    }

    // Add a SocialUser to the static HashMap
    public static void addSocialUser(SocialUser user) {
        socialUsers.put(user.getUuid(), user);
        databaseConnection.setUpdatePending(true);
    }

    // Remove a SocialUser from the static HashMap
    public static void removeSocialUser(Player player) {
        socialUsers.remove(player.getUniqueId());
        databaseConnection.setUpdatePending(true);
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
        return player.getPlayer();
    }
    public void updatePlayerInstance(){
        this.player.updatePlayerInstance();
    }
    public UUID getUuid() {
        return uuid;
    }
    public String getUsername() {
        return username;
    }
    public String getDateFirstJoined(){
        return this.dateFirstJoined;
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
    public void setUsername(String username) {
        this.username = username;
        databaseConnection.setUpdatePending(true);
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


    @Override
    public String toString(){
        return username + " " + uuid + " Friends: " + friendsList.toString();
    }

}
