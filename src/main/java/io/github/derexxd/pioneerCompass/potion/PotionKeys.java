package io.github.derexxd.pioneerCompass.potion;

import io.github.derexxd.pioneerCompass.PioneerCompass;
import org.bukkit.NamespacedKey;

public final class PotionKeys {

    private final NamespacedKey teleportationPotion;

    public PotionKeys(PioneerCompass plugin) {
        this.teleportationPotion = new NamespacedKey(plugin, "teleportation_potion");
    }

    public NamespacedKey teleportationPotion() {
        return teleportationPotion;
    }
}
