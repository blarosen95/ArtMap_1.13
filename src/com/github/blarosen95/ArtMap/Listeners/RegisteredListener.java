package com.github.blarosen95.ArtMap.Listeners;

import org.bukkit.event.Listener;

public interface RegisteredListener extends Listener {
    void unregister();
}
