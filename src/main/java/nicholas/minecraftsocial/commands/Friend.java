package nicholas.minecraftsocial.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nicholas.minecraftsocial.helper.MessageHandler;
import nicholas.minecraftsocial.SocialUser;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

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

        Player player = (Player) commandSender;
        Component message = Component.text("");

        MessageHandler.chatLegacyMessage(player, "Friend Commands:", true);

        message = message.append(Component.text("/friend list - List all of your friends."));
        message = message.append(Component.newline());
        message = message.append(Component.text("/friend add <player> - Send a friend request to a player."));
        message = message.append(Component.newline());
        message = message.append(Component.text("/friend remove <player> - Remove a player from your friends list."));
        message = message.append(Component.newline());
        message = message.append(Component.text("/friend accept <player> - Accept a friend request from a player."));
        message = message.append(Component.newline());
        message = message.append(Component.text("/friend deny <player> - Deny a friend request from a player."));

        MessageHandler.chatMessage(player, message, false, true);
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
    private void denyRequest(SocialUser sender, SocialUser target) throws SQLException {

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

    // Sends player a list of their friends
    private void listFriends(SocialUser sender) throws SQLException {

        Component message = getFriendsListComponent(sender);
        if(message == null){
            message = Component.text("No friends in friends list.").color(NamedTextColor.YELLOW);
        }

        MessageHandler.chatMessage(sender.getPlayer(), message, true);
    }

    // Creates a component for user's friend list. Names appear green if the friend is online.
    private Component getFriendsListComponent(SocialUser user) throws SQLException {
        Component component = null;
        ArrayList<UUID> friendsList = user.getFriendsList();

        SocialUser friend;
        for(int i=0; i<friendsList.size(); i++){
            friend = SocialUser.getSocialUser(friendsList.get(i));

            component = Component.text(friend.getUsername());
            if(friend.getPlayer().isOnline()){
                component = component.append(component.color(NamedTextColor.GREEN));
            }else{
                component = component.append(component.color(NamedTextColor.GRAY));
            }

            if(i != friendsList.size()-1){
                component = component.append(Component.text(", "));
            }
        }
        return component;
    }

    // Check if the player has permission to use the command
    private boolean checkPermission(Player player, String permission){
        if(!player.hasPermission("minecraftsocial."+permission) && !player.isOp()) {
            MessageHandler.noPermission(player);
            return false;
        }
        return true;
    }

}
