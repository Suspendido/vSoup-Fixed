package kami.gg.souppvp.tier.util;

import kami.gg.souppvp.tier.TierCategory;

/*
 * Copyright (c) 2026. @Comunidad, made since 23/6/2026
 * Use or redistribution of this source file is only permitted
 * if explicit permission is given by the author.
 */
public class TierUtils {

    public static int calculateNextTierXP(int currentTier) {
        int baseXP = 100;
        int increment = 500;

        if (currentTier == 0) return baseXP;
        if (currentTier <= 5) return baseXP + (currentTier * increment);
        if (currentTier <= 10) return 3000 + ((currentTier - 5) * 1000);
        if (currentTier <= 15) return 8000 + ((currentTier - 10) * 1000);
        if (currentTier <= 20) return 13000 + ((currentTier - 15) * 2000);

        return 23000 + ((currentTier - 20) * 3000);
    }

    public static int calculateTierReward(int tier) {
        if (tier == 0) return 0;
        if (tier <= 5) return 500;
        if (tier <= 10) return 1000;
        if (tier <= 15) return 2000;
        if (tier <= 20) return 3000;
        return 5000;
    }

    public static boolean isNewCategory(int oldTier, int newTier) {
        TierCategory oldCategory = TierCategory.getCategoryByLevel(oldTier);
        TierCategory newCategory = TierCategory.getCategoryByLevel(newTier);
        return oldCategory != newCategory;
    }
}
