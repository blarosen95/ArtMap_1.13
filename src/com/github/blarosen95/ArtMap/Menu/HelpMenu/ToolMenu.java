package com.github.blarosen95.ArtMap.Menu.HelpMenu;

import com.github.blarosen95.ArtMap.ArtMap;
import com.github.blarosen95.ArtMap.Config.Lang;
import com.github.blarosen95.ArtMap.Menu.API.BasicMenu;
import com.github.blarosen95.ArtMap.Menu.API.ChildMenu;
import com.github.blarosen95.ArtMap.Menu.Button.Button;
import com.github.blarosen95.ArtMap.Menu.Button.LinkedButton;
import com.github.blarosen95.ArtMap.Menu.Button.StaticButton;
import com.github.blarosen95.ArtMap.Menu.Handler.CacheableMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

public class ToolMenu extends BasicMenu implements ChildMenu {

    public ToolMenu() {
        super(ChatColor.DARK_BLUE + Lang.MENU_TOOLS.get(), InventoryType.HOPPER);
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return ArtMap.getMenuHandler().MENU.HELP.get(viewer);
    }

    @Override
    public Button[] getButtons() {
        return new Button[]{
                new StaticButton(Material.SIGN, Lang.Array.INFO_TOOLS.get()),
                new LinkedButton(ArtMap.getMenuHandler().MENU.DYES, Material.INK_SACK, 1, Lang.Array.TOOL_DYE.get()),
                new StaticButton(Material.BUCKET, Lang.Array.TOOL_PAINTBUCKET.get()),
                new StaticButton(Material.COAL, Lang.Array.TOOL_COAL.get()),
                new StaticButton(Material.FEATHER, Lang.Array.TOOL_FEATHER.get()),
                new StaticButton(Material.COMPASS, Lang.Array.TOOL_COMPASS.get())
        };
    }
}
