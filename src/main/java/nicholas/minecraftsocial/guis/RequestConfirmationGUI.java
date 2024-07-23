package nicholas.minecraftsocial.guis;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nicholas.minecraftsocial.MinecraftSocial;
import nicholas.minecraftsocial.models.SocialUser;
import nicholas.minecraftsocial.commons.commons;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class RequestConfirmationGUI implements InventoryHolder {

    private final Inventory inv;
    private final SocialUser user;
    private final SocialUser requester;

    public RequestConfirmationGUI(SocialUser user, SocialUser requester) {
        this.user = user;
        this.requester = requester;

        Component title = Component.text("Accept or Decline Request?").color(NamedTextColor.YELLOW);
        this.inv = Bukkit.createInventory(this, 9, title);

        initializeItems();
    }

    private void initializeItems() {
        inv.setItem(3, createButton(Material.RED_STAINED_GLASS, "Decline", NamedTextColor.RED));
        inv.setItem(5, createButton(Material.GREEN_STAINED_GLASS, "Accept", NamedTextColor.GREEN));
    }

    private ItemStack createButton(Material material, String name, NamedTextColor color) {
        return commons.createButton(material, name, color);
    }

    public static void handleRequestConfirmationClick(InventoryClickEvent event, RequestConfirmationGUI gui) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();

        if (clickedItem.getType() == Material.GREEN_STAINED_GLASS) {
            commons.acceptRequest(gui.user, gui.requester);
            player.closeInventory();
        } else if (clickedItem.getType() == Material.RED_STAINED_GLASS) {
            commons.denyRequest(gui.user, gui.requester);
            player.closeInventory();
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
