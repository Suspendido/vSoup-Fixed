package kami.gg.souppvp.feats.quest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum QuestType {
    STARTER("Starter", "&a", "airdrops give {player} 5"),
    INTERMEDIATE("Intermediate", "&b", "mcrate give {player} Seasonal 2"),
    ADVANCED("Advanced", "&4", "mcrate give {player} Seasonal 10");

    private final String displayName;
    private final String color;
    private final String winningCommand;
}
