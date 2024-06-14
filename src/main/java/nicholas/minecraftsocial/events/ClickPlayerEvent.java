package nicholas.minecraftsocial.events;

import nicholas.minecraftsocial.guis.PlayerPublicProfileGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class ClickPlayerEvent implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEntityEvent e) {
        // Check if the interaction is with a player
        if(e.getRightClicked() instanceof Player) {
            Player target = (Player) e.getRightClicked();
            Player source = e.getPlayer();

            // Check if the action is performed with the main hand
            if(source.isSneaking() && e.getHand() == EquipmentSlot.HAND) {
                PlayerPublicProfileGUI gui = new PlayerPublicProfileGUI(source, target);
                source.openInventory(gui.getInventory());
            }
        }
    }

}
