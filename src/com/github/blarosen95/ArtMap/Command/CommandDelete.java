package com.github.blarosen95.ArtMap.Command;

import com.github.blarosen95.ArtMap.ArtMap;
import com.github.blarosen95.ArtMap.Config.Lang;
import com.github.blarosen95.ArtMap.IO.MapArt;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class CommandDelete extends AsyncCommand {

    CommandDelete() {
        super(null, "/artmap delete <title>", true);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
        MapArt art = ArtMap.getArtDatabase().getArtwork(args[1]);

        if (art == null) {
            msg.message = String.format(Lang.MAP_NOT_FOUND.get(), args[1]);
            return;
        }
        if (sender instanceof Player
                && !(art.getArtistPlayer().getUniqueId().equals(((Player) sender).getUniqueId())
                || sender.hasPermission("artmap.admin"))) {
            msg.message = Lang.NO_PERM.get();
            return;
        }
        if (ArtMap.getArtDatabase().deleteArtwork(art)) {
            msg.message = String.format(Lang.DELETED.get(), args[1]);
        } else {
            msg.message = String.format(Lang.MAP_NOT_FOUND.get(), args[1]);
        }
    }
}
