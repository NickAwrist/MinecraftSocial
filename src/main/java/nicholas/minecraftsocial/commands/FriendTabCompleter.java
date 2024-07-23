package nicholas.minecraftsocial.commands;

import nicholas.minecraftsocial.models.SocialUser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FriendTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return Arrays.asList("add", "remove", "accept", "deny", "list", "help");
        }else if(strings.length == 2){

            Player player = (Player) commandSender;
            SocialUser user = SocialUser.getSocialUser(player.getUniqueId());

            switch(strings[0].toLowerCase()){
                case "add":
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList());
                case "remove":
                    return user.getFriendsList().stream()
                            .map(uuid -> {
                                Player friend = Bukkit.getPlayer(uuid);
                                return (friend != null && !friend.getName().equals(user.getUsername())) ? friend.getName() : null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                case "accept":
                case "deny": {
                    return user.getIncomingRequests().stream()
                            .map(uuid -> {
                                Player friend = Bukkit.getPlayer(uuid);
                                return (friend != null && !friend.getName().equals(user.getUsername())) ? friend.getName() : null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                }
            }
        }
        return new ArrayList<>();
    }
}
