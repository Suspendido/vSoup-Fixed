package kami.gg.souppvp.util.command;

import kami.gg.souppvp.util.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@Setter
@Getter
public abstract class Argument {

    protected List<String> names;
    protected String usage;
    protected String permissible;
    protected boolean async;

    public Argument(CommandManager manager, List<String> names) {
        super();
        this.names = names;
        this.usage = null;
        this.async = false;
        this.permissible = null;
    }

    public abstract String usage();

    public void sendMessage(CommandSender sender, String... s) {
        for (String string : s) {
            sender.sendMessage(CC.t(string));
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

    public void sendUsage(CommandSender sender) {
        if (usage == null) usage = usage();
        sender.sendMessage(CC.t(usage()));
    }

    public void execute(CommandSender sender, String[] args) {
        sendMessage(sender, "woah command argument!");
    }

    public List<String> tabComplete(CommandSender sender, String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}