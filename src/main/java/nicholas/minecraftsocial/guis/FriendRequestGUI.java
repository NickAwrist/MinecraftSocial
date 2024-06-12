package nicholas.minecraftsocial.guis;

import net.kyori.adventure.text.Component;
import nicholas.minecraftsocial.MinecraftSocial;
import nicholas.minecraftsocial.SocialUser;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FriendRequestGUI implements Listener, InventoryHolder {

    private final Inventory inv;

    public FriendRequestGUI() {
        Component title = Component.text("Friend Requests");
        this.inv = MinecraftSocial.getPlugin().getServer().createInventory(this, 9, title);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inv;
    }

    private void initializeItems() {
        inv.setItem(4, new ItemStack(Material.LIME_WOOL));
        inv.setItem(6, new ItemStack(Material.RED_WOOL));
    }

    private void onClick(SocialUser sender, SocialUser target){
        
    }
}
