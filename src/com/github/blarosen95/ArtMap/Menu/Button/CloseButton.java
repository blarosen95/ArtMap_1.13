package com.github.blarosen95.ArtMap.Menu.Button;

import com.github.blarosen95.ArtMap.ArtMap;
import com.github.blarosen95.ArtMap.Config.Lang;
import com.github.blarosen95.ArtMap.Menu.Event.MenuCloseReason;
import com.github.Fupery.InvMenu.Utils.SoundCompat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class CloseButton extends Button {

    public CloseButton() {
        super(Material.BARRIER, Lang.Array.HELP_CLOSE.get());
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        SoundCompat.UI_BUTTON_CLICK.play(player, 1, 3);
        ArtMap.getMenuHandler().closeMenu(player, MenuCloseReason.BACK);
    }
}
