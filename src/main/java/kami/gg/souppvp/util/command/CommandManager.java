package kami.gg.souppvp.util.command;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.changelog.command.*;
import kami.gg.souppvp.command.*;
import kami.gg.souppvp.command.admin.*;
import kami.gg.souppvp.command.admin.statistics.*;
import kami.gg.souppvp.command.bounty.*;
import kami.gg.souppvp.command.credit.*;
import kami.gg.souppvp.command.RepairCommand;
import kami.gg.souppvp.command.spawn.*;
import kami.gg.souppvp.events.command.EventCommand;
import kami.gg.souppvp.feats.leaderboard.command.LeaderboardCommand;
import kami.gg.souppvp.feats.soupsays.command.SoupSaysCommand;
import kami.gg.souppvp.feats.staff.command.*;
import kami.gg.souppvp.map.command.MapCommand;
import kami.gg.souppvp.feats.storage.StorageStatsCommand;
import kami.gg.souppvp.command.KitEditCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommandManager {
    private final SoupPvP instance;
    private final List<Command> commands;

    public CommandManager(SoupPvP instance) {
        this.instance = instance;
        this.commands = new ArrayList<>();
        this.load();
        this.register();
    }

    private void load() {
        commands.addAll(Arrays.asList(
                // Staff
                new StaffCommand(this),
                new StaffOnlineCommand(this),
                new VanishCommand(this),
                new FreezeCommand(this),
                new HideStaffCommand(this),
                new BuildCommand(this),
                new SetCuboidCommand(this),
                new StorageStatsCommand(this),
                new FreeKitsCommand(this),
                new SoupPvPCommand(this),
                new MapCommand(this),
                new KitEditCommand(this),
                new ChangeLogAdminCommand(this),

                // Normal
                new SpawnCommand(this),
                new OPLeaveCommand(this),
                new RepairCommand(this),
                new HostCommand(this),
                new JuggernautCommand(this),
                new KillstreakCommand(this),
                new OptionsCommand(this),
                new ShopCommand(this),
                new StatisticsCommand(this),
                new TiersCommand(this),
                new SetKillstreakCommand(this),
                new PerksCommand(this),
                new TogglePerksCommand(this),
                new LeaderboardCommand(this),
                new KitsCommand(this),
                new MarketCommand(this),
                new ChangeLogCommand(this),

                // Event
                new EventCommand(this),
                new SoupSaysCommand(this),

                // Credits
                new CreditsSetCommand(this),
                new CreditsAddCommand(this),
                new CreditsPayCommand(this),

                // Bounty
                new BountyListCommand(this),
                new BountyCommand(this),

                // Stats
                new CoinflipCommand(this),
                new SetKillsCommand(this),
                new SetBountyCommand(this),
                new SetDeathsCommand(this),
                new SetExpCommand(this),
                new SetCreditsCommand(this),
                new SetLevelCommand(this)
        ));
    }

    private void register() {
        CommandMap map = getCommandMap();

        map.registerAll("souppvp",
                commands.stream()
                        .map(Command::asBukkitCommand)
                        .collect(Collectors.toList()));
    }

    public void reload() {
        // Unregister all commands first
        commands.forEach(Command::unregister);

        // Clear and reload
        commands.clear();
        this.load();
        this.register();
    }

    private CommandMap getCommandMap() {
        try {
            Method method = Bukkit.getServer().getClass().getMethod("getCommandMap");
            return (CommandMap) method.invoke(Bukkit.getServer());
        } catch (Exception e) {
            throw new RuntimeException("Failed to access CommandMap for command registration.", e);
        }
    }
}