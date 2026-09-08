package io.github.derexxd.teleportationPotion.potion;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PotionJoinListener implements Listener {

    private final TeleportationPotionItem potionItem;

    public PotionJoinListener(TeleportationPotionItem potionItem) {
        this.potionItem = potionItem;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            return;
        }
        player.getInventory().addItem(potionItem.create());
    }
}
