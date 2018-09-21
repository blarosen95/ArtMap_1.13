package com.github.blarosen95.ArtMap.Menu.HelpMenu;

import com.github.blarosen95.ArtMap.ArtMap;
import com.github.blarosen95.ArtMap.Config.Lang;
import com.github.blarosen95.ArtMap.Menu.API.BasicMenu;
import com.github.blarosen95.ArtMap.Menu.Button.Button;
import com.github.blarosen95.ArtMap.Menu.Button.LinkedButton;
import com.github.blarosen95.ArtMap.Menu.Button.StaticButton;
import com.github.blarosen95.ArtMap.Menu.Handler.MenuHandler;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryType;

import static org.bukkit.Material.*;

public class HelpMenu extends BasicMenu {

    public static final String CLICK = ChatColor.GREEN + Lang.BUTTON_CLICK.get();

    public HelpMenu() {
        super(ChatColor.DARK_BLUE + Lang.MENU_HELP.get(), InventoryType.HOPPER);
    }

    @Override
    public Button[] getButtons() {
        MenuHandler.MenuList list = ArtMap.getMenuHandler().MENU;
        return new Button[]{
                new StaticButton(SIGN, Lang.Array.HELP_GETTING_STARTED.get()),
                new LinkedButton(list.RECIPE, WORKBENCH, Lang.Array.HELP_RECIPES.get()),
                new LinkedButton(list.DYES, INK_SACK, 1, Lang.Array.HELP_DYES.get()),
                new LinkedButton(list.TOOLS, BOOK_AND_QUILL, Lang.Array.HELP_TOOLS.get()),
                new LinkedButton(list.ARTIST, PAINTING, Lang.Array.HELP_LIST.get())
        };
    }
}
