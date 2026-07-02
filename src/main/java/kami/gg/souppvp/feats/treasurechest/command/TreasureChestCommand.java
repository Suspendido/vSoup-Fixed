package kami.gg.souppvp.feats.treasurechest.command;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.feats.treasurechest.TreasureChest;
import kami.gg.souppvp.feats.treasurechest.TreasureChestHandler;
import kami.gg.souppvp.lang.Lang;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.command.Argument;
import kami.gg.souppvp.util.command.Command;
import kami.gg.souppvp.util.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Copyright (c) 2026. @Comunidad, made since 1/7/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
*/
public class TreasureChestCommand extends Command {

    public TreasureChestCommand(CommandManager manager) {
        super(manager, "chest");
        this.handleArguments(
                new CreateArgument(manager),
                new DeleteArgument(manager),
                new SetupCentralArgument(manager),
                new GenerateChestsArgument(manager),
                new EndCurrentArgument(manager),
                new ReloadArgument(manager),
                new GiveArgument(manager),
                new TakeArgument(manager),
                new ShowArgument(manager)
        );
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }

    @Override
    public List<String> usage() {
        return List.of(
                "&6/chest create <id> <displayName> <material> <slot>",
                "&6/chest delete <id>",
                "&6/chest setupcentral <id>",
                "&6/chest generatechests <id> [count] [radius]",
                "&6/chest endcurrent",
                "&6/chest reload",
                "&6/chest give <treasure> <target> <amount>",
                "&6/chest take <treasure> <target> <amount>",
                "&6/chest show <treasure> <target>"
        );
    }

    public static class EndCurrentArgument extends Argument {
        public EndCurrentArgument(CommandManager manager) {
            super(manager, List.of("endcurrent"));
            this.permissible = "op";
        }

        @Override
        public String usage() {
            return "&6/chest endcurrent";
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (!(sender instanceof Player player)) return;

            TreasureChestHandler handler = SoupPvP.getInstance().getTreasureChestHandler();
            handler.getTreasureChestListener().end(player);
            sendMessage(player, Lang.TREASURE_CHEST_CMD_END_CURRENT);
        }
    }

    public static class ReloadArgument extends Argument {
        public ReloadArgument(CommandManager manager) {
            super(manager, List.of("reload"));
            this.permissible = "op";
        }

        @Override
        public String usage() {
            return "&6/chest reload";
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            TreasureChestHandler handler = SoupPvP.getInstance().getTreasureChestHandler();
            handler.getTreasureChests().clear();
            handler.loadData(false);
            sendMessage(sender, Lang.TREASURE_CHEST_CMD_RELOAD);
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            return Collections.emptyList();
        }
    }

    public static class GiveArgument extends Argument {
        public GiveArgument(CommandManager manager) {
            super(manager, List.of("give"));
            this.permissible = "op";
        }

        @Override
        public String usage() {
            return "&6/chest give <treasure> <target> <amount>";
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (args.length < 3) {
                sendUsage(sender);
                return;
            }

            TreasureChest treasureChest = SoupPvP.getInstance().getTreasureChestHandler().getTreasureChests()
                    .stream()
                    .filter(tc -> tc.getId().equalsIgnoreCase(args[0]))
                    .findFirst()
                    .orElse(null);

            if (treasureChest == null) {
                sendMessage(sender, Lang.TREASURE_CHEST_CMD_NOT_FOUND);
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                sendMessage(sender, Lang.PLAYER_NOT_FOUND);
                return;
            }

            Integer amount = getInt(args[2]);
            if (amount == null) {
                sendMessage(sender, Lang.INVALID_NUMBER);
                return;
            }

            for (String line : Lang.TREASURE_CHEST_CMD_GIVE_TARGET) {
                target.sendMessage(Lang.format(line, "chest_name", treasureChest.getDisplayName(), "amount", String.valueOf(amount)));
            }

            target.sendTitle(treasureChest.getDisplayName(), Lang.format("&7You have been given &f{amount}x &7{chest_name}&7!", "amount", String.valueOf(amount), "chest_name", treasureChest.getDisplayName()));

            sendMessage(sender, Lang.format(Lang.TREASURE_CHEST_CMD_GIVE_SENDER, "target", target.getName(), "amount", String.valueOf(amount), "chest_name", treasureChest.getDisplayName()));

            treasureChest.getCache().put(target.getUniqueId(), treasureChest.getCache().getOrDefault(target.getUniqueId(), 0) + amount);
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            if (args.length == 1) {
                return SoupPvP.getInstance().getTreasureChestHandler().getTreasureChests().stream()
                        .map(TreasureChest::getId)
                        .toList();
            }
            if (args.length == 2) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            }
            return Collections.emptyList();
        }
    }

    public static class TakeArgument extends Argument {
        public TakeArgument(CommandManager manager) {
            super(manager, List.of("take"));
            this.permissible = "op";
        }

        @Override
        public String usage() {
            return "&6/chest take <treasure> <target> <amount>";
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (args.length < 3) {
                sendUsage(sender);
                return;
            }

            TreasureChest treasureChest = SoupPvP.getInstance().getTreasureChestHandler().getTreasureChests()
                    .stream()
                    .filter(tc -> tc.getId().equalsIgnoreCase(args[0]))
                    .findFirst()
                    .orElse(null);

            if (treasureChest == null) {
                sendMessage(sender, Lang.TREASURE_CHEST_CMD_NOT_FOUND);
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sendMessage(sender, Lang.PLAYER_NOT_FOUND);
                return;
            }

            Integer amount = getInt(args[2]);
            if (amount == null) {
                sendMessage(sender, Lang.INVALID_NUMBER);
                return;
            }

            target.sendMessage(Lang.format(Lang.TREASURE_CHEST_CMD_TAKE_TARGET, "amount", String.valueOf(amount), "chest_name", treasureChest.getDisplayName(), "sender", sender.getName()));
            sendMessage(sender, Lang.format(Lang.TREASURE_CHEST_CMD_TAKE_SENDER, "target", target.getName(), "amount", String.valueOf(amount), "chest_name", treasureChest.getDisplayName()));

            treasureChest.getCache().put(target.getUniqueId(), treasureChest.getCache().getOrDefault(target.getUniqueId(), 0) - amount);

            if (treasureChest.getCache().getOrDefault(target.getUniqueId(), 0) < 0) {
                treasureChest.getCache().put(target.getUniqueId(), 0);
            }
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            if (args.length == 1) {
                return SoupPvP.getInstance().getTreasureChestHandler().getTreasureChests().stream()
                        .map(TreasureChest::getId)
                        .toList();
            }
            if (args.length == 2) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            }
            return Collections.emptyList();
        }
    }

    public static class ShowArgument extends Argument {
        public ShowArgument(CommandManager manager) {
            super(manager, List.of("show"));
            this.permissible = "op";
        }

        @Override
        public String usage() {
            return "&6/chest show <treasure> <target>";
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (args.length < 2) {
                sendUsage(sender);
                return;
            }

            TreasureChest treasureChest = SoupPvP.getInstance().getTreasureChestHandler().getTreasureChests()
                    .stream()
                    .filter(tc -> tc.getId().equalsIgnoreCase(args[0]))
                    .findFirst()
                    .orElse(null);

            if (treasureChest == null) {
                sendMessage(sender, Lang.TREASURE_CHEST_CMD_NOT_FOUND);
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                sendMessage(sender, Lang.PLAYER_NOT_FOUND);
                return;
            }

            sendMessage(sender, Lang.format(Lang.TREASURE_CHEST_CMD_SHOW, "target", target.getName(), "amount", String.valueOf(treasureChest.getCache().getOrDefault(target.getUniqueId(), 0))));
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            if (args.length == 1) {
                return SoupPvP.getInstance().getTreasureChestHandler().getTreasureChests().stream()
                        .map(TreasureChest::getId)
                        .toList();
            }
            if (args.length == 2) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            }
            return Collections.emptyList();
        }
    }

    public static class CreateArgument extends Argument {
        public CreateArgument(CommandManager manager) {
            super(manager, List.of("create"));
            this.permissible = "op";
        }

        @Override
        public String usage() {
            return "&6/chest create <id> <displayName> <material> <slot>";
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (!(sender instanceof Player player)) return;
            if (args.length < 4) {
                sendUsage(sender);
                return;
            }

            String id = args[0];
            String displayName = args[1];
            Material material;
            Integer slot;

            try {
                material = Material.valueOf(args[2].toUpperCase());
            } catch (IllegalArgumentException e) {
                sendMessage(player, Lang.TREASURE_CHEST_CMD_INVALID_MATERIAL);
                return;
            }

            slot = getInt(args[3]);
            if (slot == null) {
                sendMessage(player, Lang.TREASURE_CHEST_CMD_INVALID_SLOT);
                return;
            }

            TreasureChestHandler handler = SoupPvP.getInstance().getTreasureChestHandler();

            // Check if already exists
            if (handler.getTreasureChests().stream().anyMatch(tc -> tc.getId().equalsIgnoreCase(id))) {
                sendMessage(player, Lang.TREASURE_CHEST_CMD_ALREADY_EXISTS);
                return;
            }

            TreasureChest treasureChest = new TreasureChest(id);
            treasureChest.setDisplayName(displayName);
            treasureChest.setMaterial(material);
            treasureChest.setSlot(slot);
            treasureChest.setMaxOpened(4);

            handler.getTreasureChests().add(treasureChest);
            handler.saveData();

            sendMessage(player, Lang.format(Lang.TREASURE_CHEST_CMD_CREATED, "display_name", displayName, "id", id));
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            if (args.length == 3) {
                List<String> materials = new ArrayList<>();
                for (Material m : Material.values()) {
                    materials.add(m.name());
                }
                return materials;
            }
            return Collections.emptyList();
        }
    }

    public static class DeleteArgument extends Argument {
        public DeleteArgument(CommandManager manager) {
            super(manager, List.of("delete"));
            this.permissible = "op";
        }

        @Override
        public String usage() {
            return "&6/chest delete <id>";
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (args.length < 1) {
                sendUsage(sender);
                return;
            }

            TreasureChestHandler handler = SoupPvP.getInstance().getTreasureChestHandler();
            TreasureChest treasureChest = handler.getTreasureChests()
                    .stream()
                    .filter(tc -> tc.getId().equalsIgnoreCase(args[0]))
                    .findFirst()
                    .orElse(null);

            if (treasureChest == null) {
                sendMessage(sender, Lang.TREASURE_CHEST_CMD_NOT_FOUND);
                return;
            }

            handler.getTreasureChests().remove(treasureChest);

            // Delete the file
            File file = new File(handler.getFolder(), treasureChest.getId() + ".yml");
            if (file.exists()) {
                file.delete();
            }

            handler.saveData();
            sendMessage(sender, Lang.format(Lang.TREASURE_CHEST_CMD_DELETED, "display_name", treasureChest.getDisplayName()));
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            if (args.length == 1) {
                return SoupPvP.getInstance().getTreasureChestHandler().getTreasureChests().stream()
                        .map(TreasureChest::getId)
                        .toList();
            }
            return Collections.emptyList();
        }
    }

    public static class SetupCentralArgument extends Argument {
        public SetupCentralArgument(CommandManager manager) {
            super(manager, List.of("setupcentral"));
            this.permissible = "op";
        }

        @Override
        public String usage() {
            return "&6/chest setupcentral <id>";
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (!(sender instanceof Player player)) return;
            if (args.length < 1) {
                sendUsage(sender);
                return;
            }

            TreasureChestHandler handler = SoupPvP.getInstance().getTreasureChestHandler();
            TreasureChest treasureChest = handler.getTreasureChests()
                    .stream()
                    .filter(tc -> tc.getId().equalsIgnoreCase(args[0]))
                    .findFirst()
                    .orElse(null);

            if (treasureChest == null) {
                sendMessage(player, Lang.TREASURE_CHEST_CMD_NOT_FOUND);
                return;
            }

            Location playerLoc = player.getLocation();
            Location centralLoc = playerLoc.getBlock().getLocation();

            // Set central location
            treasureChest.setCentralLocation(centralLoc.clone());

            // Place ender chest
            centralLoc.getBlock().setType(Material.ENDER_CHEST);

            // Place daylight detector 2 blocks below
            Location detectorLoc = centralLoc.clone().subtract(0, 2, 0);
            detectorLoc.getBlock().setType(Material.DAYLIGHT_DETECTOR);

            // Create hologram
            Hologram existingHologram = DHAPI.getHologram("treasure_chest_central_" + treasureChest.getId());
            if (existingHologram != null) {
                existingHologram.delete();
                existingHologram.destroy();
            }

            DHAPI.createHologram(
                    "treasure_chest_central_" + treasureChest.getId(),
                    centralLoc.clone().add(0.5, 1.4, 0.5),
                    false,
                    Lang.TREASURE_CHEST_HOLOGRAM
            );

            handler.saveData();
            sendMessage(player, Lang.format(Lang.TREASURE_CHEST_CMD_SETUP_CENTRAL, "display_name", treasureChest.getDisplayName()));
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            if (args.length == 1) {
                return SoupPvP.getInstance().getTreasureChestHandler().getTreasureChests().stream()
                        .map(TreasureChest::getId)
                        .toList();
            }
            return Collections.emptyList();
        }
    }

    public static class GenerateChestsArgument extends Argument {
        public GenerateChestsArgument(CommandManager manager) {
            super(manager, List.of("generatechests"));
            this.permissible = "op";
        }

        @Override
        public String usage() {
            return "&6/chest generatechests <id> [count] [radius]";
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (!(sender instanceof Player player)) return;
            if (args.length < 1) {
                sendUsage(sender);
                return;
            }

            TreasureChestHandler handler = SoupPvP.getInstance().getTreasureChestHandler();
            TreasureChest treasureChest = handler.getTreasureChests()
                    .stream()
                    .filter(tc -> tc.getId().equalsIgnoreCase(args[0]))
                    .findFirst()
                    .orElse(null);

            if (treasureChest == null) {
                sendMessage(player, Lang.TREASURE_CHEST_CMD_NOT_FOUND);
                return;
            }

            if (treasureChest.getCentralLocation() == null) {
                sendMessage(player, Lang.TREASURE_CHEST_CMD_NEEDS_CENTRAL);
                return;
            }

            // Parse optional parameters
            int chestCount = args.length >= 2 ? getInt(args[1]) : 4;
            int radius = args.length >= 3 ? getInt(args[2]) : 3;

            if (chestCount < 1) {
                sendMessage(player, Lang.TREASURE_CHEST_CMD_INVALID_COUNT);
                return;
            }
            if (chestCount > 8) {
                sendMessage(player, Lang.TREASURE_CHEST_CMD_MAX_CHESTS);
                return;
            }
            if (radius < 1) {
                sendMessage(player, Lang.TREASURE_CHEST_CMD_INVALID_RADIUS);
                return;
            }

            // Clear existing chests
            for (Location loc : treasureChest.getChests()) {
                loc.getBlock().setType(Material.AIR);
            }
            treasureChest.getChests().clear();

            Location center = treasureChest.getCentralLocation();

            // Distribute chests in cardinal directions pattern
            int[] distribution = calculateDistribution(chestCount);
            int chestIndex = 0;

            // Directions: North, South, East, West
            BlockFace[] directions = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
            double[][] offsets = {{0, -1}, {0, 1}, {1, 0}, {-1, 0}}; // x, z offsets for each direction
            double[][] laterals = {{1, 0}, {1, 0}, {0, 1}, {0, 1}}; // lateral offsets for multiple chests in same direction

            for (int dir = 0; dir < 4; dir++) {
                for (int i = 0; i < distribution[dir]; i++) {
                    double lateralOffset = (i - (distribution[dir] - 1) / 2.0) * 2.0; // Center the chests laterally with 2 blocks spacing
                    double x = center.getX() + offsets[dir][0] * radius + laterals[dir][0] * lateralOffset;
                    double z = center.getZ() + offsets[dir][1] * radius + laterals[dir][1] * lateralOffset;
                    Location chestLocation = new Location(center.getWorld(), x, center.getY(), z);

                    if (chestLocation.getBlock().getType() == Material.CHEST ||
                        chestLocation.getBlock().getType() == Material.TRAPPED_CHEST) {
                        sendMessage(player, Lang.format(Lang.TREASURE_CHEST_CMD_SKIPPED_OCCUPIED, "position", String.valueOf(chestIndex + 1)));
                        chestIndex++;
                        continue;
                    }

                    // Check adjacent blocks to prevent large chest formation (only check towards center direction)
                    boolean hasAdjacentChest = false;
                    BlockFace towardsCenter = getOppositeFace(directions[dir]);
                    if (chestLocation.getBlock().getRelative(towardsCenter).getType() == Material.CHEST ||
                        chestLocation.getBlock().getRelative(towardsCenter).getType() == Material.TRAPPED_CHEST) {
                        hasAdjacentChest = true;
                    }

                    if (hasAdjacentChest) {
                        sendMessage(player, Lang.format(Lang.TREASURE_CHEST_CMD_SKIPPED_ADJACENT, "position", String.valueOf(chestIndex + 1)));
                        chestIndex++;
                        continue;
                    }

                    // Place chest block
                    chestLocation.getBlock().setType(Material.CHEST);

                    // Orient chest towards center
                    double dx = center.getX() - chestLocation.getX();
                    double dz = center.getZ() - chestLocation.getZ();

                    // Determine block face based on direction to center
                    BlockFace facing;
                    if (Math.abs(dx) > Math.abs(dz)) {
                        facing = dx > 0 ? BlockFace.EAST : BlockFace.WEST;
                    } else {
                        facing = dz > 0 ? BlockFace.SOUTH : BlockFace.NORTH;
                    }

                    // Use data to set chest direction (1.8 method)
                    byte data = switch (facing) {
                        case SOUTH -> 3;
                        case WEST -> 4;
                        case EAST -> 5;
                        default -> 2;
                    };
                    chestLocation.getBlock().setData(data);

                    treasureChest.getChests().add(chestLocation.clone());
                    chestIndex++;
                }
            }

            handler.saveData();
            sendMessage(player, Lang.format(Lang.TREASURE_CHEST_CMD_GENERATED, "count", String.valueOf(chestCount), "radius", String.valueOf(radius), "display_name", treasureChest.getDisplayName()));
        }

        private int[] calculateDistribution(int chestCount) {
            int[] distribution = new int[4]; // North, South, East, West

            distribution = switch (chestCount) {
                case 1 -> new int[]{1, 0, 0, 0};
                case 2 -> new int[]{1, 1, 0, 0};
                case 3 -> new int[]{1, 1, 1, 0};
                case 4 -> new int[]{1, 1, 1, 1};
                case 5 -> new int[]{2, 1, 1, 1};
                case 6 -> new int[]{2, 2, 1, 1};
                case 7 -> new int[]{2, 2, 2, 1};
                case 8 -> new int[]{2, 2, 2, 2};
                default -> distribution;
            };

            return distribution;
        }

        private BlockFace getOppositeFace(BlockFace face) {
            return switch (face) {
                case NORTH -> BlockFace.SOUTH;
                case SOUTH -> BlockFace.NORTH;
                case EAST -> BlockFace.WEST;
                case WEST -> BlockFace.EAST;
                default -> face;
            };
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String[] args) {
            if (args.length == 1) {
                return SoupPvP.getInstance().getTreasureChestHandler().getTreasureChests().stream()
                        .map(TreasureChest::getId)
                        .toList();
            }
            if (args.length == 2) {
                return List.of("1", "2", "3", "4", "5", "6", "7", "8");
            }
            if (args.length == 3) {
                return List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
            }
            return Collections.emptyList();
        }
    }
}
