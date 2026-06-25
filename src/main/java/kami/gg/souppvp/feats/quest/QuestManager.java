package kami.gg.souppvp.feats.quest;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/*
 * Copyright (c) 2026. @Comunidad, made since 23/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
@Getter @Setter
public class QuestManager {

    private List<Quest> quests = new ArrayList<>();
    private Map<UUID, String> playerQuests = new HashMap<>();
    private Map<UUID, Integer> questStatus = new HashMap<>();

    public Quest findCurrentQuest(Player player) {
        if (playerQuests.containsKey(player.getUniqueId())) {
            return this.findQuestByName(playerQuests.get(player.getUniqueId()));
        }

        return this.findQuestByName("ClaimRankStarter");
    }

    public List<Quest> findQuestByWeight(int weight) {
        return this.quests.stream().filter(it -> it.getWeight() == weight).collect(Collectors.toList());
    }

    public void completeQuest(Player player, String questId) {
        final Quest currentQuest = this.findCurrentQuest(player);

        if (currentQuest.getQuestID().equalsIgnoreCase(questId)) {
            currentQuest.completeQuest(player);
        }
    }

    public Quest findNextQuest(Player player) {
        final Quest currentQuest = this.findCurrentQuest(player);

        int nextWeight = currentQuest.getWeight()+1;

        for (Quest loopQuest : this.findQuestByWeight(nextWeight)) {

            if (loopQuest.getWeight() != nextWeight) {
                continue;
            }

            if (loopQuest.canDoQuest(player)) {
                return loopQuest;
            }
        }

        return null;
    }

    public Quest findQuestByName(String name) {
        return this.quests.stream().filter(it -> it.getQuestID().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
