package nicholas.minecraftsocial.guis;

import net.kyori.adventure.text.Component;
import nicholas.minecraftsocial.MinecraftSocial;
import nicholas.minecraftsocial.SocialUser;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PlayerPublicProfileGUI implements Listener, InventoryHolder {

    private final Inventory inv;
    private final Player targetPlayer;
    private final OfflinePlayer offlineTargetPlayer;
    private final SocialUser targetUser;
    private final Player senderPlayer;
    private final SocialUser senderUser;

    public PlayerPublicProfileGUI(Player sender, Player target) {
        this.targetPlayer = target;
        this.targetUser = SocialUser.getSocialUserFromList(targetPlayer.getUniqueId());
        this.offlineTargetPlayer = null;

        this.senderPlayer = sender;
        this.senderUser = SocialUser.getSocialUserFromList(senderPlayer.getUniqueId());

        Component title = Component.text(targetUser.getUsername()+"'s profile");
        this.inv = MinecraftSocial.getPlugin().getServer().createInventory(this, 27, title);

        initializeItems();
    }
    public PlayerPublicProfileGUI(Player sender, OfflinePlayer target) {
        this.targetUser = SocialUser.getSocialUser(target.getUniqueId());
        this.offlineTargetPlayer = target;
        targetPlayer = null;

        this.senderPlayer = sender;
        this.senderUser = SocialUser.getSocialUserFromList(senderPlayer.getUniqueId());

        Component title = Component.text(targetUser.getUsername()+"'s profile");
        this.inv = MinecraftSocial.getPlugin().getServer().createInventory(this, 27, title);

        initializeItems();
    }

    public Player getTargetPlayer(){return this.targetPlayer;}
    public OfflinePlayer getOfflineTargetPlayer(){return this.offlineTargetPlayer;}

    @Override
    public @NotNull Inventory getInventory() {
        return this.inv;
    }

    private void initializeItems() {

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

        if(targetPlayer != null){
            skullMeta.setOwningPlayer(targetPlayer);
            skullMeta.displayName(Component.text(targetPlayer.getName()));
        }else{
            skullMeta.setOwningPlayer(offlineTargetPlayer);
            skullMeta.displayName(Component.text(offlineTargetPlayer.getName()));
        }

        playerHead.setItemMeta(skullMeta);

        ItemStack friendStatus;
        if(senderUser.getFriendsList().contains(targetUser.getUuid())){
            friendStatus = new ItemStack(Material.RED_WOOL);
            ItemMeta redMeta = friendStatus.getItemMeta();
            redMeta.displayName(Component.text("Remove Friend")); // Example meta modification
            friendStatus.setItemMeta(redMeta);
        }else{
            friendStatus = new ItemStack(Material.LIME_WOOL);
            ItemMeta limeMeta = friendStatus.getItemMeta();
            limeMeta.displayName(Component.text("Send Friend Request")); // Example meta modification
            friendStatus.setItemMeta(limeMeta);
        }

        ItemStack firstPlayedItem = new ItemStack(Material.CAKE);
        ItemMeta firstPlayedMeta = firstPlayedItem.getItemMeta();
        firstPlayedMeta.displayName(Component.text("First Login: " + targetUser.getDateFirstJoined()));
        firstPlayedItem.setItemMeta(firstPlayedMeta);


        int playTimeTicks;
        if(targetPlayer != null){
            playTimeTicks = targetPlayer.getStatistic(Statistic.PLAY_ONE_MINUTE);
        }else{
            playTimeTicks = offlineTargetPlayer.getStatistic(Statistic.PLAY_ONE_MINUTE);
        }

        long playTimeMillis = playTimeTicks * 50L;

        long hours = TimeUnit.MILLISECONDS.toHours(playTimeMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(playTimeMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(playTimeMillis) % 60;
        String playTimeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        ItemStack playTimeItem = new ItemStack(Material.CLOCK);
        ItemMeta playTimeMeta = playTimeItem.getItemMeta();
        playTimeMeta.displayName(Component.text("Total Playtime: " + playTimeString));
        playTimeItem.setItemMeta(playTimeMeta);

        inv.setItem(12, friendStatus);
        inv.setItem(13, firstPlayedItem);
        inv.setItem(14, playTimeItem);
        inv.setItem(4, playerHead);
    }


    public void open(Player player) {
        player.openInventory(inv);
    }
}
