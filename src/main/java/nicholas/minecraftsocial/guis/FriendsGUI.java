package nicholas.minecraftsocial.guis;

import net.kyori.adventure.text.Component;
import nicholas.minecraftsocial.MinecraftSocial;
import nicholas.minecraftsocial.SocialUser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FriendsGUI implements InventoryHolder {

    private Inventory inv;
    private SocialUser user;
    private Player player;

    public FriendsGUI(SocialUser user){
        this.user = user;
        this.player = user.getPlayer();

        Component title = Component.text("Friends");
        this.inv = MinecraftSocial.getPlugin().getServer().createInventory(this, 27, title);

        initializeItems();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inv;
    }

    private void initializeItems(){

        ItemStack back = new ItemStack(Material.BARRIER);

        populateFriends();

        inv.setItem(18, back);
    }

    private void populateFriends(){

        int currentPos=0;

        for(UUID friendUUID: user.getFriendsList()){
            Player friend = Bukkit.getPlayer(friendUUID);
            OfflinePlayer offlineFriend = Bukkit.getOfflinePlayer(friendUUID);

            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

            if(friend != null){
                skullMeta.setOwningPlayer(friend);
                skullMeta.displayName(Component.text(friend.getName()));
            }else{
                skullMeta.setOwningPlayer(offlineFriend);
                skullMeta.displayName(Component.text(offlineFriend.getName()));
            }

            playerHead.setItemMeta(skullMeta);

            inv.setItem(currentPos++, playerHead);
        }
    }

}
