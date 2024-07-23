package nicholas.minecraftsocial.models;

import nicholas.minecraftsocial.exceptions.PlayerNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SocialPlayer {

    private Player player;
    private OfflinePlayer offlinePlayer;

    private final UUID uuid;

    public SocialPlayer(UUID uuid) throws PlayerNotFoundException {
        this.uuid = uuid;
        initializeSocialPlayer();
    }

    private void initializeSocialPlayer() throws PlayerNotFoundException {
        Player player = Bukkit.getPlayer(uuid);
        if(player != null){
            this.player = player;
            this.offlinePlayer = null;
        } else{
            this.player = null;
            this.offlinePlayer = Bukkit.getOfflinePlayer(uuid);

            if (!this.offlinePlayer.hasPlayedBefore()) {
                throw new PlayerNotFoundException("Player with UUID " + uuid + " does not exist.");
            }
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
    }

    public String getUsername() {
        if(this.player != null){
            return this.player.getName();
        } else {
            return this.offlinePlayer.getName();
        }
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Player updatePlayerInstance(){
        this.player = Bukkit.getPlayer(uuid);
        return this.player;
    }



}
