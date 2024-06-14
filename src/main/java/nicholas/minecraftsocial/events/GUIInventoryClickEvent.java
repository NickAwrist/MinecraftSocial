package nicholas.minecraftsocial.events;

import nicholas.minecraftsocial.SocialUser;
import nicholas.minecraftsocial.commons.commons;
import nicholas.minecraftsocial.guis.PlayerPublicProfileGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIInventoryClickEvent implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        Inventory inventory = e.getInventory();
        if(inventory.getHolder() instanceof PlayerPublicProfileGUI){
            e.setCancelled(true);

            Player p = (Player) e.getWhoClicked();
            Player t = ((PlayerPublicProfileGUI) inventory.getHolder()).getTargetPlayer();

            SocialUser user = SocialUser.getSocialUserFromList(p.getUniqueId());
            SocialUser target = SocialUser.getSocialUserFromList(t.getUniqueId());


            ItemStack clickedItem = e.getCurrentItem();
            if(clickedItem == null || clickedItem.getType() == Material.AIR) return;

            switch(clickedItem.getType()) {
                case LIME_WOOL:
                    commons.addFriend(user, target);
                    p.closeInventory();
                    break;
                case RED_WOOL:
                    p.sendMessage("You declined!");
                    break;
                default:
                    break;
            }
        }

    }


}
