package nicholas.minecraftsocial.commands;

import nicholas.minecraftsocial.guis.PlayerPersonalProfileGUI;
import nicholas.minecraftsocial.helper.MessageHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Profile implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)){
            MessageHandler.debug(MessageHandler.DebugType.ERROR, "You must be a player to execute this command.");
            return true;
        }

        Player source = ((Player) commandSender).getPlayer();
        PlayerPersonalProfileGUI gui = new PlayerPersonalProfileGUI(source);
        source.openInventory(gui.getInventory());

        return true;
    }
}
