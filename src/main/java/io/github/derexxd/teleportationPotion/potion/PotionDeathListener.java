package io.github.derexxd.teleportationPotion.potion;

import io.github.derexxd.teleportationPotion.TeleportationPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class PotionDeathListener implements Listener {

    private TeleportationPotionItem teleportationPotion;

    public PotionDeathListener(TeleportationPotionItem teleportationPotion) {
        this.teleportationPotion = teleportationPotion;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!TeleportationPotion.KEEP_ON_DEATH) {
            return;
        }

        Iterator<ItemStack> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            ItemStack drop = iterator.next();
            if (teleportationPotion.isTeleportationPotion(drop)) {
                iterator.remove();
                event.getItemsToKeep().add(drop);
            }
        }
    }
}
