package nicholas.minecraftsocial.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nicholas.minecraftsocial.commons.commons;
import nicholas.minecraftsocial.exceptions.PlayerNotFoundException;
import nicholas.minecraftsocial.guis.RemoveFriendConfirmationGUI;
import nicholas.minecraftsocial.guis.RequestsGUI;
import nicholas.minecraftsocial.helper.MessageHandler;
import nicholas.minecraftsocial.models.SocialPlayer;
import nicholas.minecraftsocial.models.SocialUser;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class Friend implements CommandExecutor{

        private static Player senderPlayer;

        @Override
        public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("You must be a player to use this command.");
                return true;
            }

            if (strings.length < 1) {
                help(commandSender);
                return true;
            }

            String commandString = strings[0].toLowerCase();

            senderPlayer = ((Player) commandSender).getPlayer();

            if (senderPlayer == null) {
                return false;
            }

            SocialUser socialUserSender = SocialUser.getSocialUser(senderPlayer.getUniqueId());

            SocialPlayer targetPlayer;
            SocialUser socialUserTarget = null;

            if (strings.length == 2 && multipleArgCommand(commandString)) {
                try {
                    targetPlayer = new SocialPlayer(strings[1]);
                    socialUserTarget = SocialUser.getSocialUser(targetPlayer.getUUID());
                } catch (PlayerNotFoundException e) {
                    senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                    MessageHandler.chatError(senderPlayer, "Player not found.");
                    return true;
                }
            } else if (multipleArgCommand(commandString)) {
                MessageHandler.chatError(senderPlayer, "You must specify a player.");
                return true;
            }

            switch (commandString) {
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
                case "requests":
                    listRequests(socialUserSender);
                    break;
                default:
                    return false;
            }

            return true;
        }

        private void help(CommandSender commandSender) {
            Player player = (Player) commandSender;
            Component message = Component.text("");

            message = message.append(Component.text("/friend list - List all of your friends.").color(NamedTextColor.GRAY));
            message = message.append(Component.newline());
            message = message.append(Component.text("/friend add <player> - Send a friend request to a player.").color(NamedTextColor.GRAY));
            message = message.append(Component.newline());
            message = message.append(Component.text("/friend remove <player> - Remove a player from your friends list.").color(NamedTextColor.GRAY));
            message = message.append(Component.newline());
            message = message.append(Component.text("/friend accept <player> - Accept a friend request from a player.").color(NamedTextColor.GRAY));
            message = message.append(Component.newline());
            message = message.append(Component.text("/friend deny <player> - Deny a friend request from a player.").color(NamedTextColor.GRAY));
            message = message.append(Component.newline());
            message = message.append(Component.text("/friend requests - List all of your incoming friend requests.").color(NamedTextColor.GRAY));

            MessageHandler.chatMessage(player, message, true);
        }

    private void listRequests(SocialUser sender) {
        new RequestsGUI(sender, 0).open(sender.getPlayer());
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

        if(!sender.getFriendsList().contains(target.getUuid())){
            senderPlayer.playSound(senderPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            MessageHandler.chatError(sender.getPlayer(), "You are not friends with this player.");
            return;
        }

        new RemoveFriendConfirmationGUI(sender, target).open(sender.getPlayer());
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

    private boolean multipleArgCommand(String command){
        return !(command.equals("list") || command.equals("help") || command.equals("requests"));
    }

}
