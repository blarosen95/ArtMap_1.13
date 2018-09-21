package com.github.blarosen95.ArtMap.Menu.API;

import com.github.blarosen95.ArtMap.Menu.Handler.CacheableMenu;
import org.bukkit.entity.Player;

public interface ChildMenu extends MenuTemplate {
    CacheableMenu getParent(Player viewer);
}
