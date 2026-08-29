package io.github.derexxd.teleportationPotion.potion;

import io.github.derexxd.teleportationPotion.TeleportationPotion;
import org.bukkit.NamespacedKey;

public final class PotionKeys {

    private final NamespacedKey teleportationPotion;

    public PotionKeys(TeleportationPotion plugin) {
        this.teleportationPotion = new NamespacedKey(plugin, "teleportation_potion");
    }

    public NamespacedKey teleportationPotion() {
        return teleportationPotion;
    }
}
