package kami.gg.souppvp.kit.progress;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
public class KitProgress {

    private int kills;
    private int deaths;
    private int timesUsed;

    private int level;
    private int exp;

    private int rebirths;
    private Set<String> unlockedCosmetics = new HashSet<>();

    public KitProgress() {
        this.level = 1;
        this.exp = 0;
    }
}