package nicholas.minecraftsocial.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import nicholas.minecraftsocial.models.SocialUser;
import nicholas.minecraftsocial.helper.MessageHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class LoginEvent implements Listener {

    @EventHandler
    public void onLogin(PlayerJoinEvent e){

        // Creates a SocialUser instance for the player. If the player is new, it will create a new instance and add
        // it to the database
        MessageHandler.debug(MessageHandler.DebugType.INFO, "Creating SocialUser instance for " + e.getPlayer().getName());
        SocialUser user = SocialUser.getSocialUser(e.getPlayer().getUniqueId());
        MessageHandler.debug(MessageHandler.DebugType.INFO, "SocialUser instance created for " + e.getPlayer().getName());

        // Get the number of friends online
        SocialUser friend;
        int onlineFriends = 0;
        for(UUID uuid : user.getFriendsList()){
            friend = SocialUser.getSocialUserFromList(uuid);
            if(friend != null && friend.getPlayer().isOnline()){
                onlineFriends++;
            }
        }

        if(!user.getIncomingRequests().isEmpty()){
            Component incomingRequestsMessage = Component.text("You have ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text(user.getIncomingRequests().size())
                            .color(NamedTextColor.GREEN))
                    .append(Component.text(" friend requests. ")
                            .color(NamedTextColor.GRAY))
                    .append(Component.text("Click here to view them.")
                            .color(NamedTextColor.YELLOW)
                            .clickEvent(ClickEvent.runCommand("/friend requests")));

            // Send the message to the player
            MessageHandler.chatMessage(user.getPlayer(), incomingRequestsMessage, true);
        }

        // Send player message indicating the number of friends they have online
        Component message = createMessage(onlineFriends);

        MessageHandler.chatMessage(user.getPlayer(), message, true);
    }

    private static Component createMessage(int onlineFriends) {
        Component message = Component.text("You have ")
                .color(NamedTextColor.GRAY);
        if(onlineFriends > 0){
            message = message.append(Component.text(onlineFriends)
                    .color(NamedTextColor.GREEN));
        }else{
            message = message.append(Component.text(onlineFriends)
                    .color(NamedTextColor.YELLOW));
        }
        message = message.append(Component.text(" friends online.")
                .color(NamedTextColor.GRAY));
        return message;
    }

}