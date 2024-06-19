package nicholas.minecraftsocial.guis;

import net.kyori.adventure.text.Component;
import nicholas.minecraftsocial.MinecraftSocial;
import nicholas.minecraftsocial.SocialUser;
import nicholas.minecraftsocial.helper.MessageHandler;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class PlayerPersonalProfileGUI implements InventoryHolder {

    private final Inventory inv;
    SocialUser ownerUser;
    Player ownerPlayer;

    public PlayerPersonalProfileGUI(Player owner){

        this.ownerUser = SocialUser.getSocialUserFromList(owner.getUniqueId());
        this.ownerPlayer = owner.getPlayer();

        Component title = Component.text(ownerUser.getUsername()+"'s profile");
        this.inv = MinecraftSocial.getPlugin().getServer().createInventory(this, 27, title);

        MessageHandler.debug("ERROR", "Friends: "+ownerUser.getFriendsList().toString());

        initializeItems();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inv;
    }

    private void initializeItems() {

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
        skullMeta.setOwningPlayer(ownerPlayer);
        skullMeta.displayName(Component.text(ownerPlayer.getName()));
        playerHead.setItemMeta(skullMeta);


        ItemStack firstPlayedItem = new ItemStack(Material.CAKE);
        ItemMeta firstPlayedMeta = firstPlayedItem.getItemMeta();
        firstPlayedMeta.displayName(Component.text("First Login: " + ownerUser.getDateFirstJoined()));
        firstPlayedItem.setItemMeta(firstPlayedMeta);

        int playTimeTicks = ownerPlayer.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long playTimeMillis = playTimeTicks * 50L;

        long hours = TimeUnit.MILLISECONDS.toHours(playTimeMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(playTimeMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(playTimeMillis) % 60;
        String playTimeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        ItemStack playTimeItem = new ItemStack(Material.CLOCK);
        ItemMeta playTimeMeta = playTimeItem.getItemMeta();
        playTimeMeta.displayName(Component.text("Total Playtime: " + playTimeString));
        playTimeItem.setItemMeta(playTimeMeta);

        ItemStack friends = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = friends.getItemMeta();
        meta.displayName(Component.text("Friends"));
        friends.setItemMeta(meta);


        inv.setItem(12, friends);
        inv.setItem(13, firstPlayedItem);
        inv.setItem(14, playTimeItem);
        inv.setItem(4, playerHead);
    }

    public void open(Player player) {
        player.openInventory(inv);
    }
}
