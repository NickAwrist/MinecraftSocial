package nicholas.minecraftsocial.events;

import nicholas.minecraftsocial.SocialUser;
import nicholas.minecraftsocial.commons.commons;
import nicholas.minecraftsocial.guis.FriendsGUI;
import nicholas.minecraftsocial.guis.PlayerPersonalProfileGUI;
import nicholas.minecraftsocial.guis.PlayerPublicProfileGUI;
import nicholas.minecraftsocial.guis.RemoveFriendConfirmationGUI;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

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

}

