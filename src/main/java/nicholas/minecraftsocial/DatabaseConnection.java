package nicholas.minecraftsocial;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import nicholas.minecraftsocial.helper.SocialUserTypeAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseConnection {

    private Connection connection;
    private final String type;
    private final File jsonFile;
    private final Gson gson;
    private final Type userType = new TypeToken<Map<UUID, SocialUser>>() {}.getType();

    private static final String URL = MinecraftSocial.getPlugin().getConfig().getString("DatabaseType.mysql.host");
    private static final String USER = MinecraftSocial.getPlugin().getConfig().getString("DatabaseType.mysql.username");
    private static final String PASSWORD = MinecraftSocial.getPlugin().getConfig().getString("DatabaseType.mysql.password");

    public DatabaseConnection(String type) {
        this.type = type;
        this.jsonFile = new File(MinecraftSocial.getPlugin().getDataFolder(), "database.json");

        this.gson = new GsonBuilder()
                .registerTypeAdapter(SocialUser.class, new SocialUserTypeAdapter())
                .create();

        if (type.equals("json") && !jsonFile.exists()) {
            try {
                MinecraftSocial.getPlugin().getDataFolder().mkdirs();
                jsonFile.createNewFile();
                try (Writer writer = new FileWriter(jsonFile)) {
                    gson.toJson(new HashMap<UUID, SocialUser>(), writer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Connect to database
    public void connect() throws SQLException {
        if (type.equals("json")) {
            return;
        }

        if (connection != null && !connection.isClosed()) {
            return;
        }
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Disconnect from database
    public void disconnect() throws SQLException {
        if (type.equals("json")) {
            return;
        }

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Get the Social User object from MySQL database
    public SocialUser getSocialUserSQL(UUID uuid) throws SQLException {
        String query = "SELECT * FROM users WHERE uuid = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setString(1, uuid.toString());
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            Bukkit.getLogger().severe("ERROR: Player not found");
            return null;
        }

        ResultSet result = statement.executeQuery();

        // If the user exists in the database, return their data.
        if (result.next()) {
            // Get the friends list from the database and convert it to an ArrayList of UUIDs
            ArrayList<String> uuidStrings = gson.fromJson(result.getString("friend_uuids"), ArrayList.class);
            ArrayList<UUID> friendsList = new ArrayList<>();
            if (uuidStrings != null) {
                for (String uuidString : uuidStrings) {
                    friendsList.add(UUID.fromString(uuidString));
                }
            }

            // Get the incoming friend requests from the database and convert them to ArrayLists of UUIDs
            uuidStrings = gson.fromJson(result.getString("incoming_requests"), ArrayList.class);
            ArrayList<UUID> incomingRequests = new ArrayList<>();
            if (uuidStrings != null) {
                for (String uuidString : uuidStrings) {
                    incomingRequests.add(UUID.fromString(uuidString));
                }
            }

            // Get the outgoing friend requests from the database and convert them to ArrayLists of UUIDs
            uuidStrings = gson.fromJson(result.getString("outgoing_requests"), ArrayList.class);
            ArrayList<UUID> outgoingRequests = new ArrayList<>();
            if (uuidStrings != null) {
                for (String uuidString : uuidStrings) {
                    outgoingRequests.add(UUID.fromString(uuidString));
                }
            }

            return new SocialUser(player, friendsList, incomingRequests, outgoingRequests);

            // If the user does not exist, create a fresh social user and add it to the database. (brand-new users)
        } else {
            SocialUser newUser = new SocialUser(player);
            addNewUser(newUser);

            return newUser;
        }
    }

    // Get the Social User object from JSON file
    public SocialUser getSocialUserJson(UUID uuid) {
        try (Reader reader = new FileReader(jsonFile)) {
            Map<UUID, SocialUser> users = gson.fromJson(reader, userType);

            if (users == null) {
                users = new HashMap<>();
            }

            SocialUser user = users.get(uuid);

            if (user != null) {
                user.updatePlayerInstance();
                return user;
            } else {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    Bukkit.getLogger().severe("ERROR: Player not found");
                    return null;
                }

                SocialUser newUser = new SocialUser(player);
                addNewUserJson(newUser, users);

                return newUser;
            }
        } catch (EOFException e) {
            Bukkit.getLogger().severe("JSON file is empty or improperly formatted.");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Get the Social User object from the appropriate database
    public SocialUser getSocialUser(UUID uuid) throws SQLException {
        if (type.equals("json")) {
            return getSocialUserJson(uuid);
        } else if (type.equals("mysql")) {
            return getSocialUserSQL(uuid);
        }

        return null;
    }

    // Add a user to MySQL database
    public void addNewUserSQL(SocialUser user) throws SQLException {
        String query = "INSERT INTO users (uuid, username, friend_uuids) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);

        String uuid = user.getUuid().toString();
        String username = user.getUsername();

        statement.setString(1, uuid);
        statement.setString(2, username);
        statement.setString(3, "[]");
        statement.executeUpdate();
    }

    // Add a user to JSON file
    public void addNewUserJson(SocialUser user) {
        addNewUserJson(user, null);
    }

    private void addNewUserJson(SocialUser user, Map<UUID, SocialUser> users) {
        if (users == null) {
            try (Reader reader = new FileReader(jsonFile)) {
                users = gson.fromJson(reader, userType);
                if (users == null) {
                    users = new HashMap<>();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        users.put(user.getUuid(), user);

        try (Writer writer = new FileWriter(jsonFile)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add a player to the database. Used when a player logs in for the first time
    public void addNewUser(SocialUser user) throws SQLException {
        if (type.equals("json")) {
            addNewUserJson(user);
        } else if (type.equals("mysql")) {
            addNewUserSQL(user);
        }
    }

    // Remove a player from the database. Does not have a use case at the moment
    public void removePlayer(SocialUser user) throws SQLException {
        if (type.equals("json")) {
            try (Reader reader = new FileReader(jsonFile);
                 Writer writer = new FileWriter(jsonFile)) {
                Map<UUID, SocialUser> users = gson.fromJson(reader, userType);
                if (users == null) {
                    users = new HashMap<>();
                }

                users.remove(user.getUuid());

                gson.toJson(users, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String query = "DELETE FROM users WHERE uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);

            String uuid = user.getUuid().toString();

            statement.setString(1, uuid);
            statement.executeUpdate();
        }
    }

    // Update the friend list of a player in the MySQL database
    public void updateFriendListSQL(SocialUser user) throws SQLException {
        String query = "UPDATE users SET friend_uuids = ? WHERE uuid = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, gson.toJson(user.getFriendsList()));
        statement.setString(2, user.getUuid().toString());

        statement.executeUpdate();
    }

    // Update the friend list of a player in the JSON file
    public void updateFriendListJSON(SocialUser user) {
        try (Reader reader = new FileReader(jsonFile)) {
            Map<UUID, SocialUser> users = gson.fromJson(reader, userType);

            if (users == null) {
                users = new HashMap<>();
            }

            users.put(user.getUuid(), user);

            try (Writer writer = new FileWriter(jsonFile)) {
                gson.toJson(users, writer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Update the friend list of a player. Will be used when a player adds or removes a friend
    public void updateFriendList(SocialUser user) throws SQLException {
        if (type.equals("json")) {
            updateFriendListJSON(user);
        } else if (type.equals("mysql")) {
            updateFriendListSQL(user);
        }
    }
}
