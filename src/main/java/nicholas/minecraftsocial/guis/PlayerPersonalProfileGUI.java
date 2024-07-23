package nicholas.minecraftsocial.guis;

import net.kyori.adventure.text.Component;
import nicholas.minecraftsocial.MinecraftSocial;
import nicholas.minecraftsocial.models.SocialUser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import nicholas.minecraftsocial.commons.commons;

public class PlayerPersonalProfileGUI implements InventoryHolder {

    private final Inventory inv;
    SocialUser user;
    Player player;

    public PlayerPersonalProfileGUI(Player owner){

        this.user = SocialUser.getSocialUserFromList(owner.getUniqueId());
        this.player = owner.getPlayer();

        Component title = Component.text(user.getUsername()+"'s profile");
        this.inv = MinecraftSocial.getPlugin().getServer().createInventory(this, 27, title);

        initializeItems();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inv;
    }

    private void initializeItems() {

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        skullMeta.setOwningPlayer(player);
        skullMeta.displayName(Component.text(player.getName()));
        playerHead.setItemMeta(skullMeta);
        inv.setItem(4, playerHead);

        ItemStack friends = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = friends.getItemMeta();
        meta.displayName(Component.text("Friends"));
        friends.setItemMeta(meta);
        inv.setItem(12, friends);

        ItemStack firstPlayedItem = new ItemStack(Material.CAKE);
        ItemMeta firstPlayedMeta = firstPlayedItem.getItemMeta();
        firstPlayedMeta.displayName(Component.text("First Login: " + user.getDateFirstJoined()));
        firstPlayedItem.setItemMeta(firstPlayedMeta);
        inv.setItem(13, firstPlayedItem);

        ItemStack playTimeItem = new ItemStack(Material.CLOCK);
        ItemMeta playTimeMeta = playTimeItem.getItemMeta();
        playTimeMeta.displayName(Component.text("Total Playtime: " + commons.getPlayTimeString(player)));
        playTimeItem.setItemMeta(playTimeMeta);
        inv.setItem(14, playTimeItem);
    }

    public void open(Player player) {
        player.openInventory(inv);
    }
}
