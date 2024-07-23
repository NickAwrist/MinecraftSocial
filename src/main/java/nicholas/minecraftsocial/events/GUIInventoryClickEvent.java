package nicholas.minecraftsocial.events;

import nicholas.minecraftsocial.guis.*;
import nicholas.minecraftsocial.models.SocialUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIInventoryClickEvent implements Listener {

    private Inventory inventory;
    private InventoryClickEvent e;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        this.inventory = e.getInventory();
        this.e = e;

        if(inventory.getHolder() instanceof PlayerPublicProfileGUI) {
            handlePublicProfileGUI();
        }else if(inventory.getHolder() instanceof PlayerPersonalProfileGUI){
            handlePersonalProfileGUI();
        }else if(inventory.getHolder() instanceof FriendsGUI){
            handleFriendsGUI();
        }else if(inventory.getHolder() instanceof RemoveFriendConfirmationGUI){
            handleRemoveFriendConfirmationGUI();
        }else if(inventory.getHolder() instanceof RequestsGUI){
            handleRequestsGUI();
        }else if(inventory.getHolder() instanceof RequestConfirmationGUI){
            handleRequestConfirmationGUI();
        }
    }

    private void handlePublicProfileGUI(){
        e.setCancelled(true);

        PlayerPublicProfileGUI.handlePlayerPublicProfileClick(e, (PlayerPublicProfileGUI) inventory.getHolder());
    }

    private void handlePersonalProfileGUI(){
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        SocialUser user = SocialUser.getSocialUserFromList(p.getUniqueId());

        ItemStack clickedItem = e.getCurrentItem();
        if(clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if(clickedItem.getType() == Material.NAME_TAG){
            new FriendsGUI(user, 0).open(p);
        }
    }

    private void handleFriendsGUI(){
        e.setCancelled(true);

        FriendsGUI.handleFriendListClick(e, (FriendsGUI) inventory.getHolder());
    }

    private void handleRemoveFriendConfirmationGUI(){
        e.setCancelled(true);

        RemoveFriendConfirmationGUI.handleRemoveFriendConfirmationClick(e, (RemoveFriendConfirmationGUI) inventory.getHolder());
    }

    private void handleRequestsGUI(){
        e.setCancelled(true);

        RequestsGUI.handleRequestListClick(e, (RequestsGUI) inventory.getHolder());
    }

    private void handleRequestConfirmationGUI(){
        e.setCancelled(true);

        RequestConfirmationGUI.handleRequestConfirmationClick(e, (RequestConfirmationGUI) inventory.getHolder());
    }

}

