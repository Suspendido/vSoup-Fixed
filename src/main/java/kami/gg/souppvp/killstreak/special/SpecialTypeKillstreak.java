package kami.gg.souppvp.killstreak.special;

import lombok.Getter;

import java.util.List;

/*
 * Copyright (c) 2026. @Comunidad, made since 30/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
@Getter
public enum SpecialTypeKillstreak {

    REPAIR("Full Repair", List.of(
        "&7Fully repairs your armor,",
        "&7giving them maximum durability."
    )),
    NUKE("Nuke", List.of(
        "&7Will start a 10 second timer and upon finish,",
        "&7it will decimate all enemies in a 25 block radius."
    )),
    GOLDEN_APPLES("Golden Apples", List.of(
        "&7Gives you 8 golden apples for",
        "&7extra absorption hearts."
    )),
    FIRE_RESISTANCE_POTION("Fire Resistance Potion", List.of(
        "&7Gives you a potion that will apply",
        "&7fire resistance for 8 minutes."
    )),
    GRANDMA_SOUPS("Grandma Soups", List.of(
        "&7Gives you 2 Grandma Soups, that will",
        "&7instantly give maximum health on consumption."
    )),
    ATTACK_DOGS("Attack Dogs", List.of(
        "&7Spawns a squad of loyal wolves",
        "&7that attack your enemies."
    )),
    EXTRA_CREDITS("Extra Credits", List.of(
        "&7Receive an additional 1000",
        "&7extra credits."
    )),
    SECURITY_GUARD("Security Guard", List.of(
        "&7Spawns a Security Guard",
        "&7whose only goal is to protect you."
    )),
    ANGRY_SNOWMAN("Angry Snowman", List.of(
        "&fSpawns an Angry Snowman",
        "&fthat shoots strong snowballs",
        "&ffor 5 minutes."
    ));

    private final String displayName;
    private final List<String> description;

    SpecialTypeKillstreak(String displayName, List<String> description) {
        this.displayName = displayName;
        this.description = description;
    }

}
