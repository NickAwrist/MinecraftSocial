package nicholas.minecraftsocial.commons;

import nicholas.minecraftsocial.SocialUser;
import nicholas.minecraftsocial.helper.MessageHandler;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class commons {

    // Sends a friend request to the target player
    public static void addFriend(SocialUser sender, SocialUser target) {

        // Get the Player objects for the sender and target
        Player senderPlayer = sender.getPlayer();
        Player targetPlayer = target.getPlayer();

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
        if(targetPlayer.isOnline()){
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

        // Check if the sender is not friends with the target
        if(!sender.getFriendsList().contains(target.getUuid())){
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            MessageHandler.chatError(senderPlayer, "You are not friends with this player.");
            return;
        }

        // Remove the friends from each other's lists
        sender.removeFriend(target);
        target.removeFriend(sender);

        // Notify sender
        MessageHandler.chatSuccess(senderPlayer, "Friend removed successfully.");
    }

    // Sender accepting target's friend request
    public static void acceptRequest(SocialUser sender, SocialUser target) {

        // Get the Player objects for the sender and target
        Player senderPlayer = sender.getPlayer();
        Player targetPlayer = target.getPlayer();

        if(!sender.getIncomingRequests().contains(target.getUuid())){
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            MessageHandler.chatError(senderPlayer, "You do not have a friend request from this player.");
            return;
        }

        sender.removeIncomingRequest(target);
        target.removeOutgoingRequest(sender);

        if(targetPlayer.isOnline()){
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
            MessageHandler.chatLegacyMessage(targetPlayer, senderPlayer.getName() + " has accepted your friend request.", true);
        }

        sender.addFriend(target);
        target.addFriend(sender);

        MessageHandler.chatSuccess(senderPlayer, "You are now friends with " + targetPlayer.getName() + ".");
    }

    // Sender denying target's friend request
    public static void denyRequest(SocialUser sender, SocialUser target) {

        // Get the Player objects for the sender and target
        Player senderPlayer = sender.getPlayer();
        Player targetPlayer = target.getPlayer();

        if(!sender.getIncomingRequests().contains(target.getUuid())){
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            MessageHandler.chatError(senderPlayer, "You do not have a friend request from this player.");
            return;
        }

        sender.removeIncomingRequest(target);
        target.removeOutgoingRequest(sender);

        if(targetPlayer.isOnline()){
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            MessageHandler.chatLegacyMessage(targetPlayer, senderPlayer.getName() + " has denied your friend request.", true);
        }

        MessageHandler.chatSuccess(senderPlayer, "Friend request denied.");
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
