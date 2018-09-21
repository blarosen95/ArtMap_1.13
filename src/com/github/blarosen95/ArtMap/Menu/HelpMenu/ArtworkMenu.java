package com.github.blarosen95.ArtMap.Menu.HelpMenu;

import com.github.blarosen95.ArtMap.ArtMap;
import com.github.blarosen95.ArtMap.Config.Lang;
import com.github.blarosen95.ArtMap.IO.MapArt;
import com.github.blarosen95.ArtMap.Menu.API.ChildMenu;
import com.github.blarosen95.ArtMap.Menu.API.ListMenu;
import com.github.blarosen95.ArtMap.Menu.Button.Button;
import com.github.blarosen95.ArtMap.Menu.Event.MenuCloseReason;
import com.github.blarosen95.ArtMap.Menu.Handler.CacheableMenu;
import com.github.blarosen95.ArtMap.Preview.ArtPreview;
import com.github.blarosen95.ArtMap.Recipe.ArtItem;
import com.github.blarosen95.ArtMap.Utils.ItemUtils;
import com.github.blarosen95.ArtMap.Utils.VersionHandler;
import com.github.Fupery.InvMenu.Utils.SoundCompat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class ArtworkMenu extends ListMenu implements ChildMenu {
    private final UUID artist;
    private ArtistMenu parent;
    private boolean adminViewing;

    public ArtworkMenu(ArtistMenu parent, UUID artist, boolean adminViewing, int page) {
        super(processTitle(artist), page);
        this.parent = parent;
        this.adminViewing = adminViewing;
        this.artist = artist;
    }

    private static String processTitle(UUID artist) {
        String name = Bukkit.getOfflinePlayer(artist).getName();
        String title = "§1" + Lang.MENU_ARTWORKS.get();
        String processedName = String.format(title, name);
        if (processedName.length() <= 32) return processedName;
        else return (name.length() <= 30) ? "§1" + name : "§1" + name.substring(0, 29);
    }

    public static boolean isPreviewItem(ItemStack item) {
        return item != null && item.getType() == Material.MAP && item.hasItemMeta()
                && item.getItemMeta().hasLore() && item.getItemMeta().getLore().get(0).equals(ArtItem.PREVIEW_KEY);
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return parent;
    }

    @Override
    public void onMenuCloseEvent(Player viewer, MenuCloseReason reason) {
        if (reason == MenuCloseReason.SPECIAL) return;
        if (ArtMap.getBukkitVersion().getVersion() != VersionHandler.BukkitVersion.v1_8) {
            ItemStack offHand = viewer.getInventory().getItemInOffHand();
            if (isPreviewItem(offHand)) viewer.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
    }

    @Override
    protected Button[] getListItems() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(artist);
        if (player == null || !player.hasPlayedBefore()) return new Button[0];
        MapArt[] artworks = ArtMap.getArtDatabase().listMapArt(player.getUniqueId());
        Button[] buttons;

        if (artworks != null && artworks.length > 0) {
            buttons = new Button[artworks.length];

            for (int i = 0; i < artworks.length; i++) {
                buttons[i] = new PreviewButton(this, artworks[i], adminViewing);
            }

        } else {
            buttons = new Button[0];
        }
        return buttons;
    }

    private class PreviewButton extends Button {

        private final MapArt artwork;
        private final ArtworkMenu artworkMenu;

        private PreviewButton(ArtworkMenu menu, MapArt artwork, boolean adminButton) {
            super(Material.MAP);
            ItemMeta meta = artwork.getMapItem().getItemMeta();
            List<String> lore = meta.getLore();
            lore.add(HelpMenu.CLICK);
            if (adminButton) lore.add(lore.size(), ChatColor.GOLD + Lang.ADMIN_RECIPE.get());
            meta.setLore(lore);
            setItemMeta(meta);
            this.artwork = artwork;
            this.artworkMenu = menu;
        }

        @Override
        public void onClick(Player player, ClickType clickType) {

            if (clickType == ClickType.LEFT) {
                if (ArtMap.getBukkitVersion().getVersion() != VersionHandler.BukkitVersion.v1_8) {

                    ItemStack offHand = player.getInventory().getItemInOffHand();
                    if (offHand.getType() == Material.AIR || isPreviewItem(offHand)) {
                        SoundCompat.BLOCK_CLOTH_FALL.play(player);
                        ItemStack preview = artwork.getMapItem();
                        ItemMeta meta = preview.getItemMeta();
                        List<String> lore = getItemMeta().getLore();
                        lore.set(0, ArtItem.PREVIEW_KEY);
                        meta.setLore(lore);
                        preview.setItemMeta(meta);
                        ArtMap.getMenuHandler().closeMenu(player, MenuCloseReason.SPECIAL);
                        player.getInventory().setItemInOffHand(preview);
                        ArtMap.getMenuHandler().openMenu(player, this.artworkMenu);
                    } else {
                        Lang.EMPTY_HAND_PREVIEW.send(player);
                    }
                } else {
                    ArtMap.getMenuHandler().closeMenu(player, MenuCloseReason.DONE);

                    ArtMap.getScheduler().SYNC.run(() -> {
                        ArtMap.getPreviewManager().endPreview(player);
                        SoundCompat.BLOCK_CLOTH_FALL.play(player);
                        if (player.getItemInHand().getType() != Material.AIR) {
                            Lang.EMPTY_HAND_PREVIEW.send(player);
                            return;
                        }
                        ArtMap.getPreviewManager().startPreview(player, new ArtPreview(artwork));
                    });

                }
            } else if (clickType == ClickType.RIGHT) {
                if (player.hasPermission("artmap.admin")) {
                    SoundCompat.BLOCK_CLOTH_FALL.play(player);
                    ArtMap.getScheduler().SYNC.run(() -> ItemUtils.giveItem(player, artwork.getMapItem()));
                } else if (adminViewing) {
                    Lang.NO_PERM.send(player);
                }
            }
        }
    }
}
