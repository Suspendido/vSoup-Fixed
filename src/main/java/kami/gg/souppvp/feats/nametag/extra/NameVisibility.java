package kami.gg.souppvp.feats.nametag.extra;

import lombok.Getter;

@Getter
public enum NameVisibility {

    ALWAYS("always"),
    NEVER("never"),
    HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),
    HIDE_FOR_OWN_TEAM("hideForOwnTeam");

    private final String name;

    NameVisibility(String name) {
        this.name = name;
    }
}