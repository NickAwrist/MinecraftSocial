package nicholas.minecraftsocial;

import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.Gson;

/*
    The plugin only pulls from the database when the user logs in to create their SocialUser object
    and when a player adds or removes a friend.

    The plugin does not write to the database when a player logs out
    since it all happens in memory and the database is only used to store the data.
 */

public class DatabaseConnection {

    private Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/minecraftsocial";
    private static final String USER = "test_db";
    private static final String PASSWORD = "password";

    private Gson gson = new Gson();

    // Connect to database
    public void connect() throws SQLException{
        if(connection != null && !connection.isClosed()){
            return;
        }
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Disconnect from database
    public void disconnect() throws SQLException{
        if(connection != null && !connection.isClosed()){
            connection.close();
        }
    }

    // Get player from database. Will be used when a player logs in.
    // If the player does not exist create a fresh social user
    public SocialUser createSocialUser(Player player) throws SQLException{
        String query = "SELECT * FROM users WHERE uuid = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        String playerUUID = player.getUniqueId().toString();
        statement.setString(1, playerUUID);

        ResultSet result = statement.executeQuery();

        // If the user exists in the database, return their data.
        if(result.next()){
            ArrayList<UUID> friendsList = gson.fromJson(result.getString("friend_uuids"), ArrayList.class);
            String username = result.getString("username");

            return new SocialUser(player, username, friendsList);

        // If the user does not exist, create a fresh social user and add it to the database. (brand-new users)
        }else{
            SocialUser newUser = new SocialUser(player);
            addPlayer(newUser);

            return newUser;
        }
    }

    // Add a player to the database. Used when a player logs in for the first time
    public void addPlayer(SocialUser user) throws SQLException{
        String query = "INSERT INTO users (uuid, username, friend_uuids) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);

        String uuid = user.getUuid().toString();
        String username = user.getUsername();

        statement.setString(1, uuid);
        statement.setString(2, username);
        statement.setString(3, "[]");
        statement.executeUpdate();
    }

    // Remove a player from the database. Does not have a use case at the moment
    public void removePlayer(SocialUser user) throws SQLException{
        String query = "DELETE FROM users WHERE uuid = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        String uuid = user.getUuid().toString();

        statement.setString(1, uuid);
        statement.executeUpdate();
    }

    // Update the friend list of a player. Will be used when a player adds or removes a friend
    public void updateFriendList(SocialUser user, SocialUser friend, boolean add) throws SQLException{
        if(add){
            user.addFriend(friend);
        }else{
            user.removeFriend(friend);
        }

        String query = "UPDATE users SET friend_uuids = ? WHERE uuid = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, gson.toJson(user.getFriendsList()));
        statement.setString(2, user.getUuid().toString());

        statement.executeUpdate();
    }
}
