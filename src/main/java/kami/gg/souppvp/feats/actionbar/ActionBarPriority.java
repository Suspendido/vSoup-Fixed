package kami.gg.souppvp.feats.actionbar;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActionBarPriority {

    STAFF(4),
    DEATH(3),
    ABILITY(2),
    GENERAL(1);

    private final int level;
}