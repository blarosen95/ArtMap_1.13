package com.github.blarosen95.ArtMap.Listeners;

import com.github.blarosen95.ArtMap.ArtMap;
import com.github.blarosen95.ArtMap.Easel.Easel;
import com.github.blarosen95.ArtMap.Easel.EaselMap;
import com.github.blarosen95.ArtMap.Utils.ChunkLocation;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;

class ChunkUnloadListener implements RegisteredListener {

    @EventHandler
    public void onChunkUnload(final ChunkUnloadEvent event) {
        EaselMap easels = ArtMap.getEasels();
        if (!easels.isEmpty() && event.getChunk() != null) {
            ChunkLocation chunk = new ChunkLocation(event.getChunk());
            ArtMap.getScheduler().ASYNC.run(() -> {
                for (Location location : easels.keySet()) {
                    Easel easel = easels.get(location);
                    if (easel.getChunk().equals(chunk)) {
                        easels.remove(location);
                    }
                }
            });
        }
    }

    @Override
    public void unregister() {
        ChunkUnloadEvent.getHandlerList().unregister(this);
    }
}
