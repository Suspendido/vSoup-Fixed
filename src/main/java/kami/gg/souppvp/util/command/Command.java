package kami.gg.souppvp.util.command;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.TaskUtil;
import kami.gg.souppvp.util.command.extra.TabCompletion;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class Command {

    protected final CommandManager manager;
    protected String name;
    protected String permissible;
    protected BukkitCommand bukkitCommand;
    protected boolean async;

    protected Map<String, Argument> arguments;
    protected List<TabCompletion> completions;
    protected List<String> usage;

    public Command(CommandManager manager, String name) {
        this.manager = manager;
        this.name = name;

        this.permissible = null;
        this.async = false;

        this.arguments = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.completions = new ArrayList<>();
    }

    // Helper para acceder a la instancia del plugin
    protected SoupPvP getInstance() {
        return manager.getInstance();
    }

    public abstract List<String> aliases();

    public abstract List<String> usage();

    public BukkitCommand asBukkitCommand() {
        if (bukkitCommand != null) return bukkitCommand;

        BukkitCommand command = new BukkitCommand(name) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String s, String[] args) {
                if (permissible != null && !permissible.isEmpty() && !sender.hasPermission(permissible)) {
                    sendMessage(sender, CC.translate("&cYou dont have permission to do this!"));
                    return true;
                }

                if (async) {
                    TaskUtil.run(() -> Command.this.execute(sender, args));
                    return true;
                }

                Command.this.execute(sender, args);
                return true;
            }

            @Override
            public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) throws IllegalArgumentException {
                List<String> tabComplete = Command.this.tabComplete(sender, args);
                if (tabComplete != null) return tabComplete;

                List<String> toTab = super.tabComplete(sender, alias, args);
                Iterator<String> iterator = toTab.iterator();

                while (iterator.hasNext()) {
                    String next = iterator.next();
                    Player player = Bukkit.getPlayer(next);

                    // Descomentar si tienes StaffManager
                    // if (player != null && getInstance().getStaffManager().isVanished(player)) {
                    //     iterator.remove();
                    // }
                }

                return toTab;
            }
        };

        if (!aliases().isEmpty()) {
            command.setAliases(aliases());
        }

        this.bukkitCommand = command;
        return command;
    }

    public void unregister() {
        if (name.equalsIgnoreCase("soupvp")) return;
        if (bukkitCommand == null) return;

        HandlerList.unregisterAll((Plugin) getInstance());

        for (Argument argument : arguments.values()) {
            HandlerList.unregisterAll((Plugin) argument);
        }

        try {
            Object server = Bukkit.getServer();
            Method getCommandMapMethod = server.getClass().getMethod("getCommandMap");
            CommandMap commandMap = (CommandMap) getCommandMapMethod.invoke(server);

            bukkitCommand.unregister(commandMap);

            Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, org.bukkit.command.Command> knownCommands = (Map<String, org.bukkit.command.Command>) knownCommandsField.get(commandMap);

            knownCommands.values().removeIf(cmd -> cmd.getName().equalsIgnoreCase(bukkitCommand.getName()));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to unregister command: " + name, e);
        }
    }

    public void handleArguments(Argument... arguments) {
        for (Argument argument : arguments) {
            argument.getNames().forEach(s -> this.arguments.put(s, argument));
        }
    }

    public void handleArguments(List<Argument> arguments) {
        for (Argument argument : arguments) {
            argument.getNames().forEach(s -> this.arguments.put(s, argument));
        }
    }

    public void sendMessage(CommandSender sender, String... s) {
        for (String msg : s) {
            sender.sendMessage(CC.translate(msg));
        }
    }

    public void sendUsage(CommandSender sender) {
        if (usage == null) usage = usage();

        for (String string : usage) {
            sender.sendMessage(CC.translate(string));
        }
    }

    public Integer getInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double getDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Float getFloat(String string) {
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String[] array = Arrays.copyOfRange(args, 1, args.length);

        if (arguments.containsKey(args[0])) {
            Argument arg = arguments.get(args[0]);

            if (permissible != null && !permissible.isEmpty() && !sender.hasPermission(permissible)) {
                sendMessage(sender, CC.translate("&cYou dont have permission to do this!"));
                return;
            }

            if (arg.permissible != null && !arg.permissible.isEmpty() && !sender.hasPermission(arg.permissible)) {
                sendMessage(sender, CC.translate("&cYou dont have permission to do this!"));
                return;
            }

            if (arg.isAsync()) {
                TaskUtil.run(() -> arg.execute(sender, array));
                return;
            }

            arg.execute(sender, array);
            return;
        }

        sendUsage(sender);
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        String string = args[args.length - 1];

        if (args.length == 1 && !arguments.isEmpty()) {
            List<String> toComplete = new ArrayList<>();

            for (Argument arg : arguments.values()) {
                if (hasPerm(sender, arg)) {
                    toComplete.addAll(arg.getNames());
                }
            }

            return toComplete
                    .stream()
                    .filter(s -> s.regionMatches(true, 0, string, 0, string.length()))
                    .collect(Collectors.toList());
        }

        if (!arguments.isEmpty()) {
            String[] array = Arrays.copyOfRange(args, 1, args.length);
            Argument arg = arguments.get(args[0]);

            if (arg == null) return null;

            List<String> tabComplete = arg.tabComplete(sender, array);

            if (hasPerm(sender, arg) && tabComplete != null && !tabComplete.isEmpty()) {
                return tabComplete;
            }
        }

        if (!completions.isEmpty()) {
            if (permissible != null && !sender.hasPermission(permissible)) return null;

            List<String> toComplete = new ArrayList<>();

            for (TabCompletion completion : completions) {
                if (completion.getArg() != args.length - 1) continue;
                if (completion.getPermission() != null && !sender.hasPermission(completion.getPermission())) continue;

                toComplete.addAll(completion.getNames());
            }

            if (!toComplete.isEmpty())
                return toComplete
                        .stream()
                        .filter(s -> s.regionMatches(true, 0, string, 0, string.length()))
                        .collect(Collectors.toList());
        }

        return null;
    }

    private boolean hasPerm(CommandSender sender, Argument arg) {
        if (arg.getPermissible() == null && permissible == null) return true;
        if (arg.getPermissible() != null && sender.hasPermission(arg.getPermissible())) return true;
        if (permissible != null && sender.hasPermission(permissible) && arg.getPermissible() != null &&
                !sender.hasPermission(arg.getPermissible())) return false;

        return permissible != null && sender.hasPermission(permissible);
    }
}