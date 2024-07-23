package nicholas.minecraftsocial.guis;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nicholas.minecraftsocial.MinecraftSocial;
import nicholas.minecraftsocial.models.SocialPlayer;
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

public class RequestsGUI implements InventoryHolder {

    private final Inventory inv;
    private static SocialUser user = null;
    private final Player player;
    private final int currentPage;

    private final int BACK_BUTTON_POS;
    private final int NEXT_BUTTON_POS;

    private final int requestsListSize;

    public RequestsGUI(SocialUser user, int page) {
        RequestsGUI.user = user;
        this.player = user.getPlayer();
        this.currentPage = page;

        Component title = Component.text("Friend Requests - Page " + (page + 1)).color(NamedTextColor.GOLD);
        this.requestsListSize = user.getIncomingRequests().size();

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
        int rows = (int) Math.ceil(requestsListSize / 9.0) + 1;
        return Math.min(rows, 6);
    }

    private void initializeItems() {
        populateRequests();

        inv.setItem(BACK_BUTTON_POS, createButton(Material.RED_STAINED_GLASS, "<- Back", NamedTextColor.RED));

        if (currentPage < (int) (requestsListSize / 46.0)) {
            inv.setItem(NEXT_BUTTON_POS, createButton(Material.GREEN_STAINED_GLASS, "Next ->", NamedTextColor.GREEN));
        }
    }

    private void populateRequests() {
        List<ItemStack> onlineRequests = new ArrayList<>();
        List<ItemStack> offlineRequests = new ArrayList<>();

        final Component ONLINE_PREFIX = Component.text("[ONLINE] ").color(NamedTextColor.GREEN);
        final Component OFFLINE_PREFIX = Component.text("[OFFLINE] ").color(NamedTextColor.GRAY);

        for (UUID requestUUID : user.getIncomingRequests()) {
            SocialUser requester = SocialUser.getSocialUser(requestUUID);
            if (requester != null) {
                Player requesterPlayer = Bukkit.getPlayer(requestUUID);
                OfflinePlayer offlineRequester = Bukkit.getOfflinePlayer(requestUUID);

                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();

                if (requesterPlayer != null) {
                    skullMeta.setOwningPlayer(requesterPlayer);
                    Component displayName = Component.text(requesterPlayer.getName());
                    skullMeta.displayName(ONLINE_PREFIX.append(displayName));
                    onlineRequests.add(playerHead);
                } else {
                    skullMeta.setOwningPlayer(offlineRequester);
                    Component displayName = Component.text(offlineRequester.getName());
                    skullMeta.displayName(OFFLINE_PREFIX.append(displayName));
                    offlineRequests.add(playerHead);
                }
                playerHead.setItemMeta(skullMeta);
            }
        }

        List<ItemStack> allRequests = new ArrayList<>(onlineRequests);
        allRequests.addAll(offlineRequests);

        int startIndex = currentPage * 45;
        int currentPos = 0;
        for (int i = startIndex; i < allRequests.size() && currentPos < 45; i++) {
            inv.setItem(currentPos++, allRequests.get(i));
        }
    }

    public static void handleRequestListClick(InventoryClickEvent event, RequestsGUI gui) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (event.getSlot() == gui.BACK_BUTTON_POS) {
            if (gui.currentPage == 0) {
                new PlayerPersonalProfileGUI(user.getPlayer()).open(user.getPlayer());
            } else {
                new RequestsGUI(user, gui.currentPage - 1).open(gui.player);
            }
        } else if (event.getSlot() == gui.NEXT_BUTTON_POS) {
            new RequestsGUI(user, gui.currentPage + 1).open(gui.player);
        } else if (clickedItem.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) clickedItem.getItemMeta();

            if (meta.getOwningPlayer() == null) return;

            SocialPlayer offlineSocialPlayer;
            try {
                offlineSocialPlayer = new SocialPlayer(meta.getOwningPlayer().getUniqueId());
            } catch (Exception e) {
                return;
            }

            new RequestConfirmationGUI(user, new SocialUser(offlineSocialPlayer)).open(user.getPlayer());
        }
    }

    public void open(Player player) {
        player.openInventory(inv);
    }
}
