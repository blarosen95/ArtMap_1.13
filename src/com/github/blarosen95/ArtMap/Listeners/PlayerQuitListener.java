package com.github.blarosen95.ArtMap.Listeners;

import com.github.blarosen95.ArtMap.ArtMap;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

class PlayerQuitListener implements RegisteredListener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (ArtMap.getArtistHandler().containsPlayer(player)) {
            ArtMap.getArtistHandler().getCurrentSession(player).removeKit(player);
            ArtMap.getArtistHandler().removePlayer(player);
        }
        if (ArtMap.getPreviewManager().isPreviewing(player)) {
            if (event.getPlayer().getItemInHand().getType() == Material.MAP) {
                ArtMap.getPreviewManager().endPreview(player);
            }
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        if (ArtMap.getArtistHandler().containsPlayer(player)) {
            ArtMap.getArtistHandler().getCurrentSession(player).removeKit(player);
            ArtMap.getArtistHandler().removePlayer(player);
        }
        if (ArtMap.getPreviewManager().isPreviewing(player)) {
            if (event.getPlayer().getItemInHand().getType() == Material.MAP) {
                ArtMap.getPreviewManager().endPreview(player);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        Player player = event.getEntity();
        if (ArtMap.getArtistHandler().containsPlayer(player)) {
            event.setKeepInventory(true);
            ArtMap.getArtistHandler().getCurrentSession(player).removeKit(player);
            ArtMap.getArtistHandler().removePlayer(player);
        }
        ArtMap.getPreviewManager().endPreview(player);
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (ArtMap.getArtistHandler().containsPlayer(event.getPlayer())) {
            if (event.getPlayer().isInsideVehicle()) {
                ArtMap.getArtistHandler().removePlayer(event.getPlayer());
            }
        }
    }

    @Override
    public void unregister() {
        PlayerQuitEvent.getHandlerList().unregister(this);
        PlayerDeathEvent.getHandlerList().unregister(this);
        PlayerTeleportEvent.getHandlerList().unregister(this);
    }
}
