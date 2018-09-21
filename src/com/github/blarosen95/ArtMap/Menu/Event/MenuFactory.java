package com.github.blarosen95.ArtMap.Menu.Event;

import com.github.blarosen95.ArtMap.Menu.Handler.CacheableMenu;
import org.bukkit.entity.Player;

public interface MenuFactory {
    CacheableMenu get(Player viewer);
}
