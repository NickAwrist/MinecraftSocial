package nicholas.minecraftsocial.commands;

import nicholas.minecraftsocial.SocialUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class debugFriendList implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        SocialUser socialUserSender = SocialUser.getSocialUser(((Player) commandSender).getUniqueId());

        int numFriends = Integer.parseInt(strings[1]);

        if(strings[0].equals("add")){
            addFriends(socialUserSender, numFriends);
        } else if(strings[0].equals("remove")){
            removeFriends(socialUserSender, numFriends);
        }

        return true;
    }

    private void addFriends(SocialUser sender, int numFriends){
        for(int i = 0; i < numFriends; i++){
            sender.addFriend(sender);
        }
    }

    private void removeFriends(SocialUser sender, int numFriends){
        for(int i = 0; i < numFriends; i++){
            sender.removeFriend(sender);
        }
    }
}
