package nicholas.minecraftsocial.database;

import nicholas.minecraftsocial.models.SocialPlayer;
import nicholas.minecraftsocial.models.SocialUser;
import nicholas.minecraftsocial.helper.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class MySQL_DB implements DatabaseConnection {

    // Instance variables
    private Connection connection;
    private static final String URL = plugin.getConfig().getString("DatabaseType.mysql.host");
    private static final String USER = plugin.getConfig().getString("DatabaseType.mysql.username");
    private static final String PASSWORD = plugin.getConfig().getString("DatabaseType.mysql.password");
    private boolean updatePending = false;

    @Override
    public void connect() {

        MessageHandler.debug(MessageHandler.DebugType.INFO, "Connecting to MySQL database.");

        try {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            MessageHandler.debug(MessageHandler.DebugType.INFO, "Connected to MySQL database...");
            checkAndCreateTable();
        } catch (SQLException e) {
            MessageHandler.debug(MessageHandler.DebugType.ERROR, "Failed to connect from MySQL database.");
            throw new RuntimeException(e);
        }
    }

    private void checkAndCreateTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS users (" +
                "uuid VARCHAR(36) NOT NULL," +
                "username VARCHAR(16) NOT NULL," +
                "friend_uuids TEXT," +
                "incoming_requests TEXT," +
                "outgoing_requests TEXT," +
                "PRIMARY KEY (uuid)" +
                ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableQuery);
            MessageHandler.debug(MessageHandler.DebugType.INFO, "Checked and ensured users table exists.");
        } catch (SQLException e) {
            MessageHandler.debug(MessageHandler.DebugType.ERROR, "Failed to create users table.");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setUpdatePending(boolean update) {
        updatePending = update;
    }
    @Override
    public boolean needsUpdate() {
        return updatePending;
    }

    @Override
    public void disconnect() {
        MessageHandler.debug(MessageHandler.DebugType.INFO, "Disconnecting from MySQL database...");
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            MessageHandler.debug(MessageHandler.DebugType.INFO, "Disconnected from MySQL database.");
        } catch (SQLException e) {
            MessageHandler.debug(MessageHandler.DebugType.ERROR, "Failed to disconnect from MySQL database.");
            throw new RuntimeException(e);
        }
    }



    @Override
    public SocialUser getSocialUser(UUID uuid) {
        try {
            String query = "SELECT * FROM users WHERE uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, uuid.toString());

            SocialPlayer player;
            try {
                player = new SocialPlayer(uuid);
            } catch (Exception e) {
                MessageHandler.debug(MessageHandler.DebugType.ERROR, "Failed to get player instance from UUID.");
                return null;
            }

            ResultSet result = statement.executeQuery();

            // If the user exists in the database, return their data.
            if (result.next()) {
                // Extract the JSON strings from the result set
                String friendUuidsJson = result.getString("friend_uuids");
                String incomingRequestsJson = result.getString("incoming_requests");
                String outgoingRequestsJson = result.getString("outgoing_requests");
                String dateFirstJoined = result.getString("date_first_join");

                ArrayList<UUID> friendsList = convertJsonToUUIDList(friendUuidsJson);
                ArrayList<UUID> incomingRequests = convertJsonToUUIDList(incomingRequestsJson);
                ArrayList<UUID> outgoingRequests = convertJsonToUUIDList(outgoingRequestsJson);

                // Create and return the SocialUser object
                return new SocialUser(player, friendsList, incomingRequests, outgoingRequests, dateFirstJoined);

                // If the user does not exist, create a fresh social user and add it to the database. (brand-new users)
            } else {
                SocialUser newUser = new SocialUser(player);
                addUser(newUser);

                return newUser;
            }

        }catch (SQLException e) {
            MessageHandler.debug(MessageHandler.DebugType.ERROR, "Failed to get SocialUser from MySQL database.");
            throw new RuntimeException(e);
        }
    }

    @Override
    public SocialUser getSocialUserByUsername(String username) {
        try {
            String query = "SELECT * FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, username);

            ResultSet result = statement.executeQuery();

            // If the user exists in the database, return their data.
            if (result.next()) {
                // Extract the JSON strings from the result set
                String friendUuidsJson = result.getString("friend_uuids");
                String incomingRequestsJson = result.getString("incoming_requests");
                String outgoingRequestsJson = result.getString("outgoing_requests");
                String dateFirstJoined = result.getString("date_first_join");

                ArrayList<UUID> friendsList = convertJsonToUUIDList(friendUuidsJson);
                ArrayList<UUID> incomingRequests = convertJsonToUUIDList(incomingRequestsJson);
                ArrayList<UUID> outgoingRequests = convertJsonToUUIDList(outgoingRequestsJson);

                // Create the SocialPlayer object
                SocialPlayer player;
                try {
                    player = new SocialPlayer(UUID.fromString(result.getString("uuid")));
                } catch (Exception e) {
                    MessageHandler.debug(MessageHandler.DebugType.ERROR, "Failed to get player instance from UUID.");
                    return null;
                }

                // Create and return the SocialUser object
                return new SocialUser(player, friendsList, incomingRequests, outgoingRequests, dateFirstJoined);

             // If the user does not exist, return null
            } else {
                return null;
            }

        }catch (SQLException e) {
            MessageHandler.debug(MessageHandler.DebugType.ERROR, "Failed to get SocialUser from MySQL database.");
            throw new RuntimeException(e);
        }
    }

    private ArrayList<UUID> convertJsonToUUIDList(String jsonString) {
        ArrayList<String> uuidStrings = gson.fromJson(jsonString, ArrayList.class);
        ArrayList<UUID> uuidList = new ArrayList<>();
        if (uuidStrings != null) {
            for (String uuidString : uuidStrings) {
                uuidList.add(UUID.fromString(uuidString));
            }
        }
        return uuidList;
    }

    @Override
    public void addUser(SocialUser user) {
        try{
            String query = "INSERT INTO users (uuid, username, friend_uuids, incoming_requests, outgoing_requests, date_first_joined) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            String uuid = user.getUuid().toString();
            String username = user.getUsername();
            String dateFirstJoin = user.getDateFirstJoined();

            statement.setString(1, uuid);
            statement.setString(2, username);
            statement.setString(3, "[]");
            statement.setString(4, "[]");
            statement.setString(5, "[]");
            statement.setString(6, dateFirstJoin);

            statement.executeUpdate();

        }catch (SQLException e) {
            MessageHandler.debug(MessageHandler.DebugType.ERROR, "Failed to add user to MySQL database.");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeUser(SocialUser user) {
        try{
            String query = "DELETE FROM users WHERE uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            String uuid = user.getUuid().toString();

            statement.setString(1, uuid);
            statement.executeUpdate();

        }catch (SQLException e) {
            MessageHandler.debug(MessageHandler.DebugType.ERROR, "Failed to remove user from MySQL database.");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateDatabase() {
        try{
            ArrayList<SocialUser> usersInMemory = SocialUser.usersAsList();
            for(SocialUser user : usersInMemory){

                String query = "UPDATE users SET friend_uuids = ?, incoming_requests = ?, outgoing_requests = ? WHERE uuid = ?";
                PreparedStatement statement = connection.prepareStatement(query);

                statement.setString(1, gson.toJson(user.getFriendsList()));
                statement.setString(2, gson.toJson(user.getIncomingRequests()));
                statement.setString(3, gson.toJson(user.getOutgoingRequests()));
                statement.setString(4, user.getUuid().toString());

                statement.executeUpdate();

                setUpdatePending(false);
            }
        } catch (SQLException e) {
            MessageHandler.debug(MessageHandler.DebugType.ERROR, "Failed to update database in MySQL database.");
        }
    }
}
