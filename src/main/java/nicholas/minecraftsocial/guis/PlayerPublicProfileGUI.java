package nicholas.minecraftsocial.guis;

import net.kyori.adventure.text.Component;
import nicholas.minecraftsocial.MinecraftSocial;
import nicholas.minecraftsocial.SocialUser;
import nicholas.minecraftsocial.commons.commons;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class PlayerPublicProfileGUI implements Listener, InventoryHolder {

    private Inventory inv;

    // Target being the player whose profile is being viewed
    private final Player targetPlayer;
    private final OfflinePlayer offlineTargetPlayer;
    private final SocialUser targetUser;

    // Sender being the player who opened the GUI
    private Player senderPlayer;
    private SocialUser senderUser;

    public PlayerPublicProfileGUI(Player sender, Player target) {
        this.targetPlayer = target;
        this.targetUser = SocialUser.getSocialUserFromList(targetPlayer.getUniqueId());
        this.offlineTargetPlayer = null;

        setup(sender);
    }
    public PlayerPublicProfileGUI(Player sender, OfflinePlayer target) {
        this.targetUser = SocialUser.getSocialUser(target.getUniqueId());
        this.offlineTargetPlayer = target;
        this.targetPlayer = null;

        setup(sender);
    }

    private void setup(Player sender){
        this.senderPlayer = sender;
        this.senderUser = SocialUser.getSocialUserFromList(senderPlayer.getUniqueId());

        Component title = Component.text(targetUser.getUsername()+"'s profile");
        this.inv = MinecraftSocial.getPlugin().getServer().createInventory(this, 27, title);

        initializeItems();
    }

// ----------------------------------------------------

    public Player getTargetPlayer(){return this.targetPlayer;}
    public OfflinePlayer getOfflineTargetPlayer(){return this.offlineTargetPlayer;}

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
        inv.setItem(4, playerHead);

        ItemStack friendAction;
        if(senderUser.getFriendsList().contains(targetUser.getUuid())){
            friendAction = new ItemStack(Material.RED_WOOL);
            ItemMeta redMeta = friendAction.getItemMeta();
            redMeta.displayName(Component.text("Remove Friend"));
            friendAction.setItemMeta(redMeta);
        }else{
            friendAction = new ItemStack(Material.LIME_WOOL);
            ItemMeta limeMeta = friendAction.getItemMeta();
            limeMeta.displayName(Component.text("Send Friend Request"));
            friendAction.setItemMeta(limeMeta);
        }
        inv.setItem(12, friendAction);

        ItemStack firstPlayedItem = new ItemStack(Material.CAKE);
        ItemMeta firstPlayedMeta = firstPlayedItem.getItemMeta();
        firstPlayedMeta.displayName(Component.text("First Login: " + targetUser.getDateFirstJoined()));
        firstPlayedItem.setItemMeta(firstPlayedMeta);
        inv.setItem(13, firstPlayedItem);

        String playTimeString;
        if(targetPlayer != null){
            playTimeString = commons.getPlayTimeString(targetPlayer);
        }else{
            playTimeString = commons.getPlayTimeString(offlineTargetPlayer);
        }

        ItemStack playTimeItem = new ItemStack(Material.CLOCK);
        ItemMeta playTimeMeta = playTimeItem.getItemMeta();
        playTimeMeta.displayName(Component.text("Total Playtime: " + playTimeString));
        playTimeItem.setItemMeta(playTimeMeta);
        inv.setItem(14, playTimeItem);

        ItemStack friendCount = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta friendCountMeta = friendCount.getItemMeta();
        friendCountMeta.displayName(Component.text("Friends: " + targetUser.getFriendsList().size()));
        friendCount.setItemMeta(friendCountMeta);
        inv.setItem(21, friendCount);

    }

    public static void handlePlayerPublicProfileClick(InventoryClickEvent event, PlayerPublicProfileGUI gui){
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null || clickedItem.getType() == Material.AIR) return;

        switch(clickedItem.getType()) {
            case LIME_WOOL:
                commons.addFriend(gui.senderUser, gui.targetUser);
                gui.senderPlayer.closeInventory();
                break;
            case RED_WOOL:
                new RemoveFriendConfirmationGUI(gui.senderUser, gui.targetUser).open(gui.senderPlayer);
                break;
            default:
                break;
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
