package nicholas.minecraftsocial.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nicholas.minecraftsocial.commons.commons;
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

    private static Player senderPlayer;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)){
            return false;
        }

        if(strings.length < 1){
            return false;
        }

        senderPlayer = ((Player) commandSender).getPlayer();

        Player targetPlayer = null;

        if(strings.length == 2){
            targetPlayer = commandSender.getServer().getPlayer(strings[1]);
        }

        SocialUser socialUserSender = SocialUser.getSocialUser(((Player) commandSender).getUniqueId());
        SocialUser socialUserTarget = null;


        String option = strings[0].toLowerCase();
        if(strings.length == 2){
            if(targetPlayer == null){
                commandSender.sendMessage("Player not found.");
                return true;
            }
            socialUserTarget = SocialUser.getSocialUser(targetPlayer.getUniqueId());
        }


        switch (option) {
            case "add":
                assert socialUserTarget != null;
                addFriend(socialUserSender, socialUserTarget);
                break;
            case "remove":
                assert socialUserTarget != null;
                removeFriend(socialUserSender, socialUserTarget);
                break;
            case "accept":
                assert socialUserTarget != null;
                acceptRequest(socialUserSender, socialUserTarget);
                break;
            case "deny":
                assert socialUserTarget != null;
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
    private void addFriend(SocialUser sender, SocialUser target) {

        // Check permissions
        if(!commons.hasPermission(senderPlayer, "addfriend")){
            return;
        }

        commons.addFriend(sender, target);
    }

    // Removes a friend from the sender's friends list
    private void removeFriend(SocialUser sender, SocialUser target) {

        // Check permissions
        if(!commons.hasPermission(senderPlayer, "removefriend")){
            return;
        }

        commons.removeFriend(sender, target);
    }

    // Sender accepting target's friend request
    private void acceptRequest(SocialUser sender, SocialUser target) {

        // Check permissions
        if(!commons.hasPermission(senderPlayer, "acceptfriend")){
            return;
        }

        commons.acceptRequest(sender, target);
    }

    // Sender denying target's friend request
    private void denyRequest(SocialUser sender, SocialUser target) {
        commons.denyRequest(sender, target);
    }

    // Sends player a list of their friends
    private void listFriends(SocialUser sender) {

        Component message = getFriendsListComponent(sender);
        if(message == null){
            message = Component.text("No friends in friends list.").color(NamedTextColor.YELLOW);
        }

        MessageHandler.chatMessage(sender.getPlayer(), message, true);
    }

    // Creates a component for user's friend list. Names appear green if the friend is online.
    private Component getFriendsListComponent(SocialUser user) {
        ArrayList<UUID> friendsList = user.getFriendsList();

        if(friendsList.isEmpty()){
            return null;
        }

        Component component = Component.text("");
        SocialUser friend;
        for(int i=0; i<friendsList.size(); i++){
            friend = SocialUser.getSocialUser(friendsList.get(i));

            Component tempComponent = Component.text(friend.getUsername());
            if(friend.getPlayer() != null && friend.getPlayer().isOnline()){
                tempComponent = tempComponent.color(NamedTextColor.GREEN);
            }else{
                tempComponent = tempComponent.color(NamedTextColor.GRAY);
            }
            component = component.append(tempComponent);

            if(i != friendsList.size()-1){
                component = component.append(Component.text(", "));
            }
        }
        return component;
    }


}
