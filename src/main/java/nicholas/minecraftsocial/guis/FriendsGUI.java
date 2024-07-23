package nicholas.minecraftsocial.guis;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nicholas.minecraftsocial.MinecraftSocial;
import nicholas.minecraftsocial.models.SocialUser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static nicholas.minecraftsocial.commons.commons.createButton;

public class FriendsGUI implements InventoryHolder {

    private final Inventory inv;
    private static SocialUser user = null;
    private final Player player;
    private final int currentPage;

    private final int BACK_BUTTON_POS;
    private final int NEXT_BUTTON_POS;

    private final int friendsListSize;

    public FriendsGUI(SocialUser user, int page) {
        FriendsGUI.user = user;
        this.player = user.getPlayer();
        this.currentPage = page;

        Component title = Component.text("Friends - Page " + (page + 1)).color(NamedTextColor.GOLD);
        this.friendsListSize = user.getFriendsList().size();

        int numberOfRows = calculateNumberOfRows();
        this.BACK_BUTTON_POS = numberOfRows * 9 - 9;
        this.NEXT_BUTTON_POS = numberOfRows * 9 - 1;

        int numberOfSlots = Math.min(numberOfRows * 9, 54);
        this.inv = MinecraftSocial.getPlugin().getServer().createInventory(this, numberOfSlots, title);

        initializeItems();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inv;
    }

    private int calculateNumberOfRows() {
        int rows = (int) Math.ceil(friendsListSize / 9.0) + 1;
        return Math.min(rows, 6);
    }

    private void initializeItems() {
        populateFriends();

        inv.setItem(BACK_BUTTON_POS, createButton(Material.RED_STAINED_GLASS, "<- Back", NamedTextColor.RED));

        if(currentPage < (int) (friendsListSize / 46.0)) {
            inv.setItem(NEXT_BUTTON_POS, createButton(Material.GREEN_STAINED_GLASS, "Next ->", NamedTextColor.GREEN));
        }
    }


    private void populateFriends() {
        List<ItemStack> onlineFriends = new ArrayList<>();
        List<ItemStack> offlineFriends = new ArrayList<>();

        final Component ONLINE_PREFIX = Component.text("[ONLINE] ").color(NamedTextColor.GREEN);
        final Component OFFLINE_PREFIX = Component.text("[OFFLINE] ").color(NamedTextColor.GRAY);

        for (UUID friendUUID : user.getFriendsList()) {
            Player friend = Bukkit.getPlayer(friendUUID);
            OfflinePlayer offlineFriend = Bukkit.getOfflinePlayer(friendUUID);

            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

            if(friend != null) {
                skullMeta.setOwningPlayer(friend);
                Component displayName = Component.text(friend.getName());
                skullMeta.displayName(friend.isOnline() ? ONLINE_PREFIX.append(displayName) : OFFLINE_PREFIX.append(displayName));
                if(friend.isOnline()) {
                    onlineFriends.add(playerHead);
                } else{
                    offlineFriends.add(playerHead);
                }
            } else{
                skullMeta.setOwningPlayer(offlineFriend);
                Component displayName = Component.text(offlineFriend.getName());
                skullMeta.displayName(OFFLINE_PREFIX.append(displayName));
                offlineFriends.add(playerHead);
            }
            playerHead.setItemMeta(skullMeta);
        }

        List<ItemStack> allFriends = new ArrayList<>(onlineFriends);
        allFriends.addAll(offlineFriends);

        int startIndex = currentPage * 45;
        int currentPos = 0;
        for (int i = startIndex; i < allFriends.size() && currentPos < 45; i++) {
            inv.setItem(currentPos++, allFriends.get(i));
        }
    }

    public static void handleFriendListClick(InventoryClickEvent event, FriendsGUI gui) {
        ItemStack clickedItem = event.getCurrentItem();
        if(clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if(event.getSlot() == gui.BACK_BUTTON_POS) {
            if(gui.currentPage == 0) {
                new PlayerPersonalProfileGUI(user.getPlayer()).open(user.getPlayer());
            } else{
                new FriendsGUI(user, gui.currentPage - 1).open(gui.player);
            }

        } else if(event.getSlot() == gui.NEXT_BUTTON_POS) {
            new FriendsGUI(user, gui.currentPage + 1).open(gui.player);

        } else if(clickedItem.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) clickedItem.getItemMeta();
            OfflinePlayer offlinePlayer = meta.getOwningPlayer();
            new PlayerPublicProfileGUI(user.getPlayer(), offlinePlayer).open(user.getPlayer());
        }
    }

    public void open(Player player) {
        player.openInventory(inv);
    }
}
