package io.github.derexxd.teleportationPotion.potion;

import io.github.derexxd.teleportationPotion.rtp.RtpService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public final class PotionConsumeListener implements Listener {

    private final TeleportationPotionItem teleportationPotion;
    private final RtpService rtpService;

    public PotionConsumeListener(TeleportationPotionItem teleportationPotion, RtpService rtpService) {
        this.teleportationPotion = teleportationPotion;
        this.rtpService = rtpService;
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (!teleportationPotion.isTeleportationPotion(event.getItem())) {
            return;
        }
        rtpService.teleport(event.getPlayer());
    }
}
