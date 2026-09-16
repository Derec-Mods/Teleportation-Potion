package io.github.derexxd.teleportationPotion.commands;

import io.github.derexxd.teleportationPotion.potion.TeleportationPotionItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

        int amount = 1;
        if (args.length >= 1) {
            try {
                amount = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                return false;
            }
        }
        if (amount < 1) {
            return false;
        }
        amount = Math.min(amount, 64);

        ItemStack bottles = potionItem.create();
        bottles.setAmount(amount);
        player.getInventory().addItem(bottles);
        return true;
    }
}
