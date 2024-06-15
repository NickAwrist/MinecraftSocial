package nicholas.minecraftsocial.database;

import nicholas.minecraftsocial.SocialUser;
import nicholas.minecraftsocial.helper.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.*;

public class JSON_DB implements DatabaseConnection {

    private static final File file = new File(plugin.getDataFolder().getAbsolutePath()+"/data.json");
    private static boolean updatePending = false;

    // Initialize JSON_DB
    public JSON_DB() {
        if(!file.exists()) {
            try {
                MessageHandler.debug("INFO", "JSON file not found. Creating...");
                file.createNewFile();
                initializeEmptyJsonFile();
                MessageHandler.debug("INFO", "JSON file created.");
            } catch (Exception e) {
                MessageHandler.debug("ERROR", "Failed to create JSON file.");
            }
        }
    }

    private void initializeEmptyJsonFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("[]"); // Write an empty JSON array to the file
        } catch (IOException e) {
            MessageHandler.debug("ERROR", "Failed to initialize JSON file with empty array.");
        }
    }

    @Override
    public void setUpdatePending(boolean update) {
        JSON_DB.updatePending = update;
    }
    @Override
    public boolean needsUpdate() {
        return updatePending;
    }

    // Connect to JSON database
    @Override
    public void connect() {
        MessageHandler.debug("INFO", "Connected to JSON database.");
    }

    // Disconnect from JSON database
    @Override
    public void disconnect() {
        MessageHandler.debug("INFO", "Disconnected from JSON database.");
    }



    // Get a SocialUser by grabbing an arraylist of SocialUsers from the JSON file and iterating through it
    @Override
    public SocialUser getSocialUser(UUID uuid) {
        try {

            BufferedReader reader = new BufferedReader(new FileReader(file));

            SocialUser[] usersArr = gson.fromJson(reader, SocialUser[].class);
            ArrayList<SocialUser> users = new ArrayList<>(Arrays.asList(usersArr));

            for(SocialUser user: users) {
                if(user.getUuid().equals(uuid)) {
                    user.updatePlayerInstance();

                    return user;
                }
            }


        } catch (Exception e) {
            MessageHandler.debug("ERROR", "Failed to read JSON file.");

        }

        // If the player is not in the file, create a new SocialUser object and flag it for update
        Player player = Bukkit.getPlayer(uuid);

        if(player == null) {
            return null;
        }

        setUpdatePending(true);
        return new SocialUser(player);
    }

    @Override
    public void addUser(SocialUser user) {
        setUpdatePending(true);
    }

    @Override
    public void removeUser(SocialUser user) {
        setUpdatePending(true);
    }


    public void updateDatabase() {
        ArrayList<SocialUser> usersInMemory = SocialUser.usersAsList();
        ArrayList<SocialUser> usersInFile = new ArrayList<>();

        // Read current users from the file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            SocialUser[] usersArray = gson.fromJson(reader, SocialUser[].class);
            if (usersArray != null) {
                usersInFile = new ArrayList<>(Arrays.asList(usersArray));
            }
            reader.close();
        } catch (IOException e) {
            MessageHandler.debug("ERROR", "Failed to read users from JSON file.");
        }

        // Create a map for easy merging
        Map<UUID, SocialUser> userMap = new HashMap<>();
        for (SocialUser user : usersInFile) {
            userMap.put(user.getUuid(), user);
        }
        for (SocialUser user : usersInMemory) {
            userMap.put(user.getUuid(), user);
        }

        // Convert the map back to a list
        ArrayList<SocialUser> mergedUsers = new ArrayList<>(userMap.values());

        // Write the merged list back to the file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            gson.toJson(mergedUsers, writer);
            setUpdatePending(false);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            MessageHandler.debug("ERROR", "Failed to write users to JSON file.");
            throw new RuntimeException(e);
        }

    }


}
