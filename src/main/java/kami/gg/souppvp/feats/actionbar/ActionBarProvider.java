package kami.gg.souppvp.feats.actionbar;

import org.bukkit.entity.Player;

/*
 * Copyright (c) 2026. @Comunidad, made since 28/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public interface ActionBarProvider {

    String getActionBar(Player player);
    ActionBarPriority priority();
    boolean isActive(Player player);

}
