package com.github.blarosen95.ArtMap.Listeners;

import com.github.blarosen95.ArtMap.ArtMap;
import com.github.blarosen95.ArtMap.Config.Lang;
import com.github.blarosen95.ArtMap.Easel.Easel;
import com.github.blarosen95.ArtMap.Easel.EaselEffect;
import com.github.blarosen95.ArtMap.Easel.EaselEvent;
import com.github.blarosen95.ArtMap.Easel.EaselEvent.ClickType;
import com.github.blarosen95.ArtMap.Easel.EaselPart;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

class PlayerInteractEaselListener implements RegisteredListener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        callEaselEvent(player, event.getRightClicked(), event, isSneaking(player));
        checkPreviewing(player, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        Player player = event.getPlayer();
        callEaselEvent(player, event.getRightClicked(), event, ClickType.LEFT_CLICK);
        checkPreviewing(player, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        callEaselEvent(null, event.getRightClicked(), event, isSneaking(player));
        checkPreviewing(player, event);
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        callEaselEvent(event.getDamager(), event.getEntity(), event, ClickType.LEFT_CLICK);
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.getCause() == HangingBreakEvent.RemoveCause.ENTITY) {
            callEaselEvent(event.getRemover(), event.getEntity(), event, ClickType.LEFT_CLICK);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        if (checkSignBreak(event.getBlock(), event)) {
            if (!checkIsPainting(event.getPlayer(), event)) {
                Lang.ActionBar.EASEL_HELP.send(event.getPlayer());
            }
        }
        checkIsPainting(event.getPlayer(), event);
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        checkSignBreak(event.getBlock(), event);
    }

    private void callEaselEvent(Entity clicker, Entity clicked, Cancellable event, ClickType click) {
        EaselPart part = EaselPart.getPartType(clicked);
        if (part == null || part == EaselPart.SEAT || part == EaselPart.MARKER) return;
        Easel easel = Easel.getEasel(clicked.getLocation(), part);
        if (easel == null) return;

        event.setCancelled(true);

        if (clicker == null || !(clicker instanceof Player)) return;
        Player player = (Player) clicker;

        boolean interactionAllowed = (click == ClickType.SHIFT_RIGHT_CLICK) ?
                ArtMap.getCompatManager().checkBuildAllowed(player, clicked.getLocation()) :
                ArtMap.getCompatManager().checkInteractAllowed(player, clicked, click);

        if (!interactionAllowed) {
            Lang.ActionBar.NO_PERM_ACTION.send(player);
            easel.playEffect(EaselEffect.USE_DENIED);
            return;
        }

        if (!checkIsPainting(player, event)) new EaselEvent(easel, click, player).callEvent();
    }

    private ClickType isSneaking(Player player) {
        return (player.isSneaking()) ? ClickType.SHIFT_RIGHT_CLICK :
                ClickType.RIGHT_CLICK;
    }

    private boolean checkIsPainting(Player player, Cancellable event) {
        if (player.isInsideVehicle() && ArtMap.getArtistHandler().containsPlayer(player)) {
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    private void checkPreviewing(Player player, Cancellable event) {
        if (ArtMap.getPreviewManager().endPreview(player)) event.setCancelled(true);
    }

    private boolean checkSignBreak(Block block, Cancellable event) {

        if (block.getType() == Material.WALL_SIGN) {
            Sign sign = ((Sign) block.getState());

            if (sign.getLine(3).equals(EaselPart.ARBITRARY_SIGN_ID)) {

                if (ArtMap.getEasels().contains(block.getLocation())
                        || Easel.checkForEasel(block.getLocation())) {
                    event.setCancelled(true);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void unregister() {
        PlayerInteractAtEntityEvent.getHandlerList().unregister(this);
        PlayerArmorStandManipulateEvent.getHandlerList().unregister(this);
        PlayerInteractEntityEvent.getHandlerList().unregister(this);
        EntityDamageByEntityEvent.getHandlerList().unregister(this);
        HangingBreakByEntityEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        BlockPhysicsEvent.getHandlerList().unregister(this);
    }
}
