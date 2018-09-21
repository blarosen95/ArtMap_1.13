package com.github.blarosen95.ArtMap.Command;

import com.github.blarosen95.ArtMap.ArtMap;
import com.github.blarosen95.ArtMap.Config.Lang;
import com.github.blarosen95.ArtMap.IO.MapArt;
import com.github.blarosen95.ArtMap.Preview.ArtPreview;
import com.github.blarosen95.ArtMap.Utils.ItemUtils;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class CommandPreview extends AsyncCommand {

    CommandPreview() {
        super(null, "/artmap preview <title>", false);
    }

    private static boolean previewArtwork(final Player player, final MapArt art) {

        if (player.hasPermission("artmap.admin")) {
            ArtMap.getScheduler().SYNC.run(() -> {
                ItemStack currentItem = player.getItemInHand();
                player.setItemInHand(art.getMapItem());

                if (currentItem != null) {
                    ItemUtils.giveItem(player, currentItem);
                }
            });

        } else {

            ArtMap.getPreviewManager().endPreview(player);

            if (player.getItemInHand().getType() != Material.AIR) {
                return false;
            }

            ArtMap.getPreviewManager().startPreview(player, new ArtPreview(art));
        }
        return true;
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

        Player player = (Player) sender;

        MapArt art = ArtMap.getArtDatabase().getArtwork(args[1]);

        if (art == null) {
            msg.message = String.format(Lang.MAP_NOT_FOUND.get(), args[1]);
            return;
        }
        if (!previewArtwork(player, art)) {
            msg.message = Lang.EMPTY_HAND_PREVIEW.get();
            return;
        }
        msg.message = String.format(Lang.PREVIEWING.get(), args[1]);
    }
}
