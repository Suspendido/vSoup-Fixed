package kami.gg.souppvp.feats.quest;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

/*
 * Copyright (c) 2026. @Comunidad, made since 23/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public abstract class Quest implements Listener {

    public abstract String getQuestID();
    public abstract String getQuestDisplayName();
    public abstract List<String> getDescription();
    public abstract QuestType getQuestType();
    public abstract int getWeight();

    public boolean canDoQuest(Player player) {
        return true;
    }

    public Quest() {
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(this, SoupPvP.getInstance());
    }

    public void completeQuest(Player player) {
        if (!SoupPvP.getInstance().getQuestManager().findCurrentQuest(player).getQuestID().equalsIgnoreCase(getQuestID())) {
            return;
        }

        player.sendMessage("");
        player.sendMessage(CC.t("&4&lQuests"));
        player.sendMessage(CC.t("&7You have completed the &f" + this.getQuestDisplayName() + " &7quest!"));
        player.sendMessage("");

        final Quest nextQuest = SoupPvP.getInstance().getQuestManager().findNextQuest(player);

        if (nextQuest != null) {
            player.sendMessage(CC.t("&a&l* NEW QUEST *"));
            player.sendMessage(CC.t("&4&l" + nextQuest.getQuestDisplayName() + " Quest"));
            for (String description : nextQuest.getDescription()) {
                player.sendMessage(ChatColor.GRAY + description);
            }

            SoupPvP.getInstance().getQuestManager().getPlayerQuests().remove(player.getUniqueId());
            SoupPvP.getInstance().getQuestManager().getPlayerQuests().put(player.getUniqueId(), nextQuest.getQuestID());
        }

        if (nextQuest == null || !nextQuest.getQuestType().equals(this.getQuestType())) {
            player.sendMessage("");
            player.sendMessage(CC.t("&aYou have completed all of the " + getQuestType().getColor() + getQuestType().getDisplayName() + " quests!"));
            player.sendMessage("");

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getQuestType().getWinningCommand().replace("{name}", player.getName()));
        }

    }
}
