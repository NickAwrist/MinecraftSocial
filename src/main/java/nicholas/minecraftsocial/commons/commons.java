package nicholas.minecraftsocial.commons;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nicholas.minecraftsocial.models.SocialPlayer;
import nicholas.minecraftsocial.models.SocialUser;
import nicholas.minecraftsocial.helper.MessageHandler;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.concurrent.TimeUnit;

public class commons {


// COMMON FRIEND FUNCTIONS --------------------------------------------------------------------------------------------

    // Sends a friend request to the target player
    public static void addFriend(SocialUser sender, SocialUser target) {

        // Get the Player objects for the sender and target
        SocialPlayer senderSocialPlayer = sender.getSocialPlayer();
        SocialPlayer targetSocialPlayer = target.getSocialPlayer();

        Player senderPlayer = senderSocialPlayer.getPlayer();

        // Check if the sender is already friends with the target
        if(sender.getFriendsList().contains(target.getUuid())){
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            MessageHandler.chatError(senderPlayer, "You are already friends with this player.");
            return;
        }

        // Check if there is already an outgoing request to the target
        if(sender.getOutgoingRequests().contains(target.getUuid())){
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            MessageHandler.chatError(senderPlayer, "You have already sent a friend request to this player.");
            return;
        }

        // Check if there is already an incoming request from the target
        if(sender.getIncomingRequests().contains(target.getUuid())){
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            MessageHandler.chatError(senderPlayer, "This player has already sent you a friend request. Use /friend accept "+target.getUsername()+" to accept it.");
            return;
        }

        // Add the request to the sender and target's lists
        sender.addOutgoingRequest(target);
        target.addIncomingRequest(sender);

        // Notify the players
        if(targetSocialPlayer.isOnline()){
            Player targetPlayer = targetSocialPlayer.getPlayer();
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
            MessageHandler.chatLegacyMessage(targetPlayer, "You have received a friend request from " + senderPlayer.getName() + ". Use /friend accept " + senderPlayer.getName() + " to accept it.", true);
        }

        senderPlayer.playSound(senderPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
        MessageHandler.chatSuccess(senderPlayer, "Friend request sent successfully.");
    }

    // Removes a friend from the sender's friends list
    public static void removeFriend(SocialUser sender, SocialUser target) {

        // Get the Player objects for the sender
        Player senderPlayer = sender.getPlayer();

        // Remove the friends from each other's lists
        sender.removeFriend(target);
        target.removeFriend(sender);

        // Notify sender
        MessageHandler.chatSuccess(senderPlayer, "Friend removed successfully.");
    }

    // Sender accepting target's friend request
    public static void acceptRequest(SocialUser sender, SocialUser target) {

        // Get the Player objects for the sender and target
        SocialPlayer senderSocialPlayer = sender.getSocialPlayer();
        SocialPlayer targetSocialPlayer = target.getSocialPlayer();

        Player senderPlayer = senderSocialPlayer.getPlayer();

        if(!sender.getIncomingRequests().contains(target.getUuid())){
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            MessageHandler.chatError(senderPlayer, "You do not have a friend request from this player.");
            return;
        }

        sender.removeIncomingRequest(target);
        target.removeOutgoingRequest(sender);

        if(targetSocialPlayer.isOnline()){
            Player targetPlayer = targetSocialPlayer.getPlayer();
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
            MessageHandler.chatLegacyMessage(targetPlayer, senderPlayer.getName() + " has accepted your friend request.", true);
        }

        sender.addFriend(target);
        target.addFriend(sender);

        MessageHandler.chatSuccess(senderPlayer, "You are now friends with " + targetSocialPlayer.getUsername() + ".");
    }

    // Sender denying target's friend request
    public static void denyRequest(SocialUser sender, SocialUser target) {

        // Get the Player objects for the sender and target
        SocialPlayer senderSocialPlayer = sender.getSocialPlayer();
        SocialPlayer targetSocialPlayer = target.getSocialPlayer();

        Player senderPlayer = senderSocialPlayer.getPlayer();

        if(!sender.getIncomingRequests().contains(target.getUuid())){
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            MessageHandler.chatError(senderPlayer, "You do not have a friend request from this player.");
            return;
        }

        sender.removeIncomingRequest(target);
        target.removeOutgoingRequest(sender);

        if(targetSocialPlayer.isOnline()){
            Player targetPlayer = targetSocialPlayer.getPlayer();
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            MessageHandler.chatLegacyMessage(targetPlayer, senderPlayer.getName() + " has denied your friend request.", true);
        }

        MessageHandler.chatSuccess(senderPlayer, "Friend request denied.");
    }

// --------------------------------------------------------------------------------------------------------------------

    // Get the play time of a player in HH:MM:SS format
    public static String getPlayTimeString(Player player){
        int playTimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long playTimeMillis = playTimeTicks * 50L;
        long hours = TimeUnit.MILLISECONDS.toHours(playTimeMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(playTimeMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(playTimeMillis) % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    public static String getPlayTimeString(OfflinePlayer player){
        int playTimeTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long playTimeMillis = playTimeTicks * 50L;
        long hours = TimeUnit.MILLISECONDS.toHours(playTimeMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(playTimeMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(playTimeMillis) % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // Create a button ItemStack with a name and color
    public static ItemStack createButton(Material material, String name, NamedTextColor color) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name).color(color));
        item.setItemMeta(meta);
        return item;
    }

    // Check if the player has permission to use the command
    public static boolean hasPermission(Player player, String permission){
        if(!player.hasPermission("minecraftsocial."+permission) && !player.isOp()) {
            MessageHandler.noPermission(player);
            return false;
        }
        return true;
    }

}
