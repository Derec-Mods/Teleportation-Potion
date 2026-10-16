package io.github.derexxd.teleportationPotion.commands;

import io.github.derexxd.teleportationPotion.potion.TeleportationPotionItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GiveCommand implements TabExecutor {

    private final TeleportationPotionItem potionItem;

    public GiveCommand(TeleportationPotionItem potionItem) {
        this.potionItem = potionItem;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Player> targets = new ArrayList<>();
        int amount = 1;

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                return false;
            }
            targets.add(player);
        } else if (args.length == 1) {
            if (sender instanceof Player player) {
                try {
                    amount = Integer.parseInt(args[0]);
                    targets.add(player);
                } catch (NumberFormatException ignored) {
                    targets.addAll(resolveTargets(sender, args[0]));
                }
            } else {
                targets.addAll(resolveTargets(sender, args[0]));
            }
        } else {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
                return false;
            }
            targets.addAll(resolveTargets(sender, args[0]));
        }

        if (amount < 1) {
            return false;
        }
        amount = Math.min(amount, 64);

        if (targets.isEmpty()) {
            return args.length > 0 && args[0].startsWith("@");
        }

        ItemStack bottles = potionItem.create();
        bottles.setAmount(amount);
        for (Player target : targets) {
            target.getInventory().addItem(bottles.clone());
        }
        return true;
    }

    private List<Player> resolveTargets(CommandSender sender, String selector) {
        List<Player> players = new ArrayList<>();
        if (selector.startsWith("@")) {
            try {
                for (Entity entity : Bukkit.selectEntities(sender, selector)) {
                    if (entity instanceof Player player) {
                        players.add(player);
                    }
                }
            } catch (IllegalArgumentException ignored) {
            }
        } else {
            Player player = Bukkit.getPlayer(selector);
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String input = args[0].toLowerCase();
            List<String> options = new ArrayList<>();
            options.add("@a");
            options.add("@p");
            options.add("@r");
            options.add("@s");
            for (Player player : Bukkit.getOnlinePlayers()) {
                options.add(player.getName());
            }
            for (String option : options) {
                if (option.toLowerCase().startsWith(input)) {
                    completions.add(option);
                }
            }
            return completions;
        }
        if (args.length == 2) {
            List<String> completions = new ArrayList<>();
            String input = args[1].toLowerCase();
            for (String count : List.of("1", "16", "32", "64")) {
                if (count.startsWith(input)) {
                    completions.add(count);
                }
            }
            return completions;
        }
        return Collections.emptyList();
    }
}

