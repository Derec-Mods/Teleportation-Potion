package io.github.derexxd.teleportationPotion.potion;

import io.github.derexxd.teleportationPotion.TeleportationPotion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

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
        if (TeleportationPotion.STARTER_BOTTLES <= 0) {
            return;
        }
        ItemStack bottles = potionItem.create();
        bottles.setAmount(TeleportationPotion.STARTER_BOTTLES);
        player.getInventory().addItem(bottles);
    }
}
