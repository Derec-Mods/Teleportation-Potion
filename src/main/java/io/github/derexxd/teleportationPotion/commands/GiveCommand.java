package io.github.derexxd.teleportationPotion.commands;

import io.github.derexxd.teleportationPotion.potion.TeleportationPotionItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class GiveCommand implements CommandExecutor {

    private final TeleportationPotionItem potionItem;

    public GiveCommand(TeleportationPotionItem potionItem) {
        this.potionItem = potionItem;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        player.getInventory().addItem(potionItem.create());
        return true;
    }
}
