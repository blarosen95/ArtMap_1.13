package com.github.blarosen95.ArtMap.Menu.API;

import com.github.blarosen95.ArtMap.Menu.Event.MenuCloseReason;
import com.github.blarosen95.ArtMap.Menu.Handler.CacheableMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;

public abstract class BasicMenu extends CacheableMenu {

    protected BasicMenu(String heading, InventoryType type) {
        super(heading, type);
    }

    @Override
    public void onMenuOpenEvent(Player viewer) {
    }

    @Override
    public void onMenuRefreshEvent(Player viewer) {
    }

    @Override
    public void onMenuClickEvent(Player viewer, int slot, ClickType click) {
    }

    @Override
    public void onMenuCloseEvent(Player viewer, MenuCloseReason reason) {
    }
}
