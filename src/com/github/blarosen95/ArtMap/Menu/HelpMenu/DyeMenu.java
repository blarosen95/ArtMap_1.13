package com.github.blarosen95.ArtMap.Menu.HelpMenu;

import com.github.blarosen95.ArtMap.ArtMap;
import com.github.blarosen95.ArtMap.Colour.ArtDye;
import com.github.blarosen95.ArtMap.Colour.DyeType;
import com.github.blarosen95.ArtMap.Config.Lang;
import com.github.blarosen95.ArtMap.Menu.API.BasicMenu;
import com.github.blarosen95.ArtMap.Menu.API.ChildMenu;
import com.github.blarosen95.ArtMap.Menu.Button.Button;
import com.github.blarosen95.ArtMap.Menu.Button.CloseButton;
import com.github.blarosen95.ArtMap.Menu.Button.StaticButton;
import com.github.blarosen95.ArtMap.Menu.Handler.CacheableMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class DyeMenu extends BasicMenu implements ChildMenu {

    public DyeMenu() {
        super(Lang.MENU_DYES.get(), InventoryType.CHEST);
    }

    @Override
    public Button[] getButtons() {
        Button[] buttons = new Button[27];
        ArtDye[] dyes = ArtMap.getDyePalette().getDyes(DyeType.DYE);
        buttons[0] = new StaticButton(Material.SIGN, Lang.Array.INFO_DYES.get());
        buttons[26] = new CloseButton();

        for (int i = 1; i < 26; i++) {
            ArtDye dye = dyes[i - 1];
            buttons[i] = new StaticButton(dye.toItem());
        }
        return buttons;
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return ArtMap.getMenuHandler().MENU.HELP.get(viewer);
    }
}
