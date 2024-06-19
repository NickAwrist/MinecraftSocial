package nicholas.minecraftsocial.helper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import nicholas.minecraftsocial.MinecraftSocial;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MessageHandler {

    // Prefix for message. "MinecraftSocial | This is a message"
    private static final TextComponent PREFIX = Component.text("MinecraftSocial").color(NamedTextColor.GOLD)
            .append(Component.text(" | ").color(NamedTextColor.GRAY));

// PLAYER MESSAGES ----------------------------------------------------

    // Send player a normal chat message. Can use legacy color codes or all gray
    public static void chatLegacyMessage(Player player, String origin, boolean usePrefix){
        origin = "&7"+origin;
        Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(origin);
        sendChat(player, message, usePrefix);
    }

    public static void chatMessage(Player player, Component origin, boolean usePrefix){
        sendChat(player, origin, usePrefix);
    }
    public static void chatMessage(Player player, Component origin, boolean usePrefix, boolean allGray){
        if(allGray){
            origin = origin.append(origin.color(NamedTextColor.GRAY));
        }
        sendChat(player, origin, usePrefix);
    }

    // Send player a success message. All green
    public static void chatSuccess(Player player, String origin){
        Component message = Component.text(origin).color(NamedTextColor.GREEN);
        sendChat(player, message, true);
    }

    // Send player an error message. All red
    public static void chatError(Player player, String origin){
        Component message = Component.text(origin).color(NamedTextColor.RED);
        sendChat(player, message, true);
    }

    // Send player an error saying they lack permission and play villager sound
    public static void noPermission(Player player){
        chatError(player, "You do not have permission to use this command");
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }

    // General method to send a chat message to a player
    private static void sendChat(Player player, Component message, boolean usePrefix){
        if(usePrefix){
            message = PREFIX.append(message);
        }
        player.sendMessage(message);
    }

// CONSOLE MESSAGES ----------------------------------------------------

    public static void debug(DebugType type, String origin) {
        type.sendDebug(origin);
    }

    public enum DebugType {
        INFO {
            @Override
            public void sendDebug(String message) {
                plugin.getComponentLogger().info(message);
            }
        },
        DEBUG {
            @Override
            public void sendDebug(String message) {
                plugin.getComponentLogger().debug(message);
            }
        },
        WARNING {
            @Override
            public void sendDebug(String message) {
                plugin.getComponentLogger().warn(message);

            }
        },
        ERROR {
            @Override
            public void sendDebug(String message) {
                plugin.getComponentLogger().error(message);
            }
        };
        final Plugin plugin = MinecraftSocial.getPlugin();
        public abstract void sendDebug(String message);
    }

}
