package io.github.derexxd.pioneerCompass.potion;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public final class PotionConsumeListener implements Listener {

    private final TeleportationPotionItem teleportationPotion;

    public PotionConsumeListener(TeleportationPotionItem teleportationPotion) {
        this.teleportationPotion = teleportationPotion;
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (!teleportationPotion.isTeleportationPotion(event.getItem())) {
            return;
        }
    }
}
