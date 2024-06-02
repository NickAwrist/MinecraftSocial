package nicholas.minecraftsocial.commands;

import net.kyori.adventure.text.Component;
import nicholas.minecraftsocial.Messenger;
import nicholas.minecraftsocial.SocialUser;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class Friend implements CommandExecutor{


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)){
            return false;
        }

        if(strings.length < 1){
            return false;
        }

        Player targetPlayer = null;

        if(strings.length == 2){
            targetPlayer = commandSender.getServer().getPlayer(strings[1]);
        }

        try {
            SocialUser socialUserSender = SocialUser.getSocialUser(((Player) commandSender).getUniqueId());
            SocialUser socialUserTarget = null;

            if(strings.length == 2){
                if(targetPlayer == null){
                    commandSender.sendMessage("Player not found.");
                    return false;
                }
                socialUserTarget = SocialUser.getSocialUser(targetPlayer.getUniqueId());
            }

            String option = strings[0].toLowerCase();
            switch (option) {
                case "add":
                    addFriend(socialUserSender, socialUserTarget);
                    break;
                case "remove":
                    removeFriend(socialUserSender, socialUserTarget);
                    break;
                case "accept":
                    acceptRequest(socialUserSender, socialUserTarget);
                    break;
                case "deny":
                    denyRequest(socialUserSender, socialUserTarget);
                    break;
                case "help":
                    help(commandSender);
                    break;
                case "list":
                    listFriends(socialUserSender);
                    break;
                default:
                    return false;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return true;

    }

    private void help(CommandSender commandSender){

        Component empty = Component.text("");

        Messenger.sendInfo(commandSender, "Friend Commands:");
        Messenger.sendInfo(commandSender, "/friend list - List all of your friends.");
        Messenger.sendInfo(commandSender, "/friend add <player> - Send a friend request to a player.", empty);
        Messenger.sendInfo(commandSender, "/friend remove <player> - Remove a player from your friends list.", empty);
        Messenger.sendInfo(commandSender, "/friend accept <player> - Accept a friend request from a player.", empty);
        Messenger.sendInfo(commandSender, "/friend deny <player> - Deny a friend request from a player.", empty);

    }

    // Sends a friend request to the target player
    private void addFriend(SocialUser sender, SocialUser target) throws SQLException {

        // Get the Player objects for the sender and target
        Player senderPlayer = sender.getPlayer();
        Player targetPlayer = target.getPlayer();

        // Check permissions
        if(!checkPermission(senderPlayer, "addfriend")){
            return;
        }

        // Check if the sender is already friends with the target
        if(sender.getFriendsList().contains(target.getUuid())){
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            Messenger.sendError(senderPlayer, "You are already friends with this player.");
            return;
        }

        // Check if there is already an outgoing request to the target
        if(sender.getOutgoingRequests().contains(target.getUuid())){
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            Messenger.sendError(senderPlayer, "You have already sent a friend request to this player.");
            return;
        }

        // Check if there is already an incoming request from the target
        if(sender.getIncomingRequests().contains(target.getUuid())){
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            Messenger.sendError(senderPlayer, "This player has already sent you a friend request. Use /friend accept "+target.getUsername()+" to accept it.");
            return;
        }

        // Add the request to the sender and target's lists
        sender.addOutgoingRequest(target);
        target.addIncomingRequest(sender);

        // Notify the players
        if(targetPlayer.isOnline()){
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
            Messenger.sendInfo(targetPlayer, "You have received a friend request from " + senderPlayer.getName() + ". Use /friend accept " + senderPlayer.getName() + " to accept it.");
        }

        senderPlayer.playSound(senderPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
        Messenger.sendSuccess(senderPlayer, "Friend request sent successfully.");
    }

    // Removes a friend from the sender's friends list
    private void removeFriend(SocialUser sender, SocialUser target) throws SQLException {

        // Get the Player objects for the sender
        Player senderPlayer = sender.getPlayer();

        // Check permissions
        if(!checkPermission(senderPlayer, "removefriend")){
            return;
        }

        // Check if the sender is not friends with the target
        if(!sender.getFriendsList().contains(target.getUuid())){
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            Messenger.sendError(senderPlayer, "You are not friends with this player.");
            return;
        }

        // Remove the friends from each other's lists
        sender.removeFriend(target);
        target.removeFriend(sender);

        // Notify sender
        Messenger.sendSuccess(senderPlayer, "Friend removed successfully.");
    }

    // Sender accepting target's friend request
    private void acceptRequest(SocialUser sender, SocialUser target) throws SQLException {

        // Get the Player objects for the sender and target
        Player senderPlayer = sender.getPlayer();
        Player targetPlayer = target.getPlayer();

        // Check permissions
        if(!checkPermission(senderPlayer, "acceptfriend")){
            return;
        }

        if(!sender.getIncomingRequests().contains(target.getUuid())){
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            Messenger.sendError(senderPlayer, "You do not have a friend request from this player.");
            return;
        }

        sender.removeIncomingRequest(target);
        target.removeOutgoingRequest(sender);

        if(targetPlayer.isOnline()){
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
            Messenger.sendInfo(targetPlayer, senderPlayer.getName() + " has accepted your friend request.");
        }

        sender.addFriend(target);
        target.addFriend(sender);

        Messenger.sendSuccess(senderPlayer, "You are now friends with " + targetPlayer.getName() + ".");
    }

    // Sender denying target's friend request
    private void denyRequest(SocialUser sender, SocialUser target) throws SQLException {

        // Get the Player objects for the sender and target
        Player senderPlayer = sender.getPlayer();
        Player targetPlayer = target.getPlayer();

        if(!sender.getIncomingRequests().contains(target.getUuid())){
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            Messenger.sendError(senderPlayer, "You do not have a friend request from this player.");
            return;
        }

        sender.removeIncomingRequest(target);
        target.removeOutgoingRequest(sender);

        if(targetPlayer.isOnline()){
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            Messenger.sendInfo(targetPlayer, senderPlayer.getName() + " has denied your friend request.");
        }

        Messenger.sendSuccess(senderPlayer, "Friend request denied.");
    }

    private void listFriends(SocialUser sender) {
        StringBuilder friendsList = new StringBuilder();
        SocialUser friend;
        for (int i = 0; i < sender.getFriendsList().size(); i++) {

            friend = SocialUser.getSocialUserFromList(sender.getFriendsList().get(i));
            friendsList.append(friend.getUsername());

            if (i != sender.getFriendsList().size() - 1) {
                friendsList.append(", ");
            }
        }

        Messenger.sendInfo(sender.getPlayer(), "Friends: " + friendsList);
    }

    // Check if the player has permission to use the command
    private boolean checkPermission(Player player, String permission){
        if(!player.hasPermission("minecraftsocial."+permission) && !player.isOp()) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            Messenger.sendError(player, "You do not have permission to use this command.");
            return false;
        }
        return true;
    }

}
