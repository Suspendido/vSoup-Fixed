package kami.gg.souppvp.tier;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum TierCategory {

    /*
    Tier categories group levels in ranges of 10.
    Each category has a unique icon, color, material, and optional texture.
    Players can choose their preferred icon from unlocked categories.
    */

    IRON(0, 9, "Iron", "✫", "&7", Material.SKULL_ITEM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzFhNzg1OTE2ZDJkMTdjYTBlYTJhZDIzZDgwMjQ3YzdjNTAyMTQ0MzkwM2JiYWI3YjI0Yjc5MzRiNmEzNjFhYiJ9fX0="),
    GOLD(10, 19, "Gold", "★", "&e", Material.SKULL_ITEM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFiNWZkZWZmZmZmYTcwODM4MWU3MGYzNTAzYTI3NTc3MmI0NTI5NmNmOWYxNjI1YTg3ZWRjNmI2MjU0OWVmNiJ9fX0="),
    DIAMOND(20, 29, "Diamond", "✦", "&b", Material.SKULL_ITEM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2I1ZmFmNGNkODcxMzhjODcxY2M2YTg2NzU4MTdhODk5ODVhM2NiODk3MjFhNGM3NjJmZTY2NmZmNjE4MWMyNCJ9fX0="),
    EMERALD(30, 39, "Emerald", "✧", "&a", Material.SKULL_ITEM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWYxMjFmN2MxYWIxNTY3ZmYyMTk4M2ZmN2E5ZTU1YzQwYzBiODY1ZjA1MGQzN2U1ZDM1ZGVmYmFhIn19fQ=="),
    SAPPHIRE(40, 49, "Sapphire", "✪", "&9", Material.SKULL_ITEM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZThjNTExYjk2MWIyZGNhMDEyZjNhNWY1YjQ2NjA3OGEwZWMyMzgwYTc2ZDUxOWVhMzY0YTdkNmRjMjhlMWJiIn19fQ=="),
    RUBY(50, 59, "Ruby", "✯", "&c", Material.SKULL_ITEM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjgzMjM2NjM5NjA3MDM2YzFiYTM5MWMyYjQ2YTljN2IwZWZkNzYwYzhiZmEyOTk2YTYwNTU1ODJiNGRhNSJ9fX0="),
    PLATINUM(60, 69, "Platinum", "✰", "&3", Material.SKULL_ITEM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjkxYWVjYTdjMTdlNjZkODY3MjMxYjM2ZDk2ZTgzYzFlZGU3NWVhZjY3Y2NmM2E4OGRjYTE1ZDQxMTRhZTE2NyJ9fX0="),
    ONYX(70, 79, "Onyx", "✱", "&8", Material.SKULL_ITEM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTg4YmNlNDk3Y2ZhNWY2MTE4MzlmNmRhMjFjOTVkMzRlM2U3MjNjMmNjNGMzYzMxOWI1NjI3NzNkMTIxNiJ9fX0="),
    MERCURIUM(80, 89, "Mercurium", "✲", "&f", Material.SKULL_ITEM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjNlZWI0NDA0YTIyZTNjNWZiZGQ0ODM2YzcyYTdmNTljMTYxNTU4OGE5YzU3ZDI4NzE1NTQ1MzcyOGFlYSJ9fX0="),
    JADE(90, 99, "Jade", "✳", "&2", Material.SKULL_ITEM, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTZlMGQ1ZmU4N2QyZDIxZTJiZGU2Mzc0NDI1YTM0MWE3NTczYjM3NWQ1YmI3ZDQ3ZjRiOGRhZTM1Mjk3ZWE0In19fQ=="),
    FIRE(100, 100, "Fire", ".I.", "&6", Material.FLINT_AND_STEEL, null);

    private final int minLevel;
    private final int maxLevel;
    private final String name;
    private final String icon;
    private final String color;
    private final Material material;
    private final String texture;

    TierCategory(int minLevel, int maxLevel, String name,String icon, String color, Material material, String texture) {
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.material = material;
        this.texture = texture;
    }

    public static TierCategory getCategoryByLevel(int level) {
        for (TierCategory category : values()) {
            if (level >= category.minLevel && level <= category.maxLevel) {
                return category;
            }
        }
        return IRON;
    }

    public static TierCategory getCategoryByName(String name) {
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            return IRON;
        }
    }

    public String getFormattedIcon() {
        return color + icon;
    }
}
