package nicholas.minecraftsocial.guis;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nicholas.minecraftsocial.MinecraftSocial;
import nicholas.minecraftsocial.models.SocialUser;
import nicholas.minecraftsocial.commons.commons;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RemoveFriendConfirmationGUI implements InventoryHolder {

    private Inventory inv;
    private final SocialUser user;
    private final SocialUser friend;

    public RemoveFriendConfirmationGUI(SocialUser user, SocialUser friend) {
        this.user = user;
        this.friend = friend;

        Component title = Component.text("Remove " + friend.getUsername() + " as a friend?").color(NamedTextColor.RED);

        this.inv = MinecraftSocial.getPlugin().getServer().createInventory(this, 9, title);

        initializeItems();
    }

    private void initializeItems() {
        inv.setItem(3, commons.createButton(Material.RED_STAINED_GLASS, "NO", NamedTextColor.RED));
        inv.setItem(5, commons.createButton(Material.GREEN_STAINED_GLASS, "Yes", NamedTextColor.GREEN));
    }

    public static void handleRemoveFriendConfirmationClick(InventoryClickEvent event, RemoveFriendConfirmationGUI gui) {
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if(clickedItem.getType() == Material.GREEN_STAINED_GLASS) {
            commons.removeFriend(gui.user, gui.friend);
            event.getWhoClicked().closeInventory();
        } else if(clickedItem.getType() == Material.RED_STAINED_GLASS) {
            event.getWhoClicked().closeInventory();
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inv;
    }

    public void open(Player player) {
        player.openInventory(inv);
    }
}
