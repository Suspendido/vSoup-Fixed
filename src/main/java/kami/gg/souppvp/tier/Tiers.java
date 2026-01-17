package kami.gg.souppvp.tier;

import lombok.Getter;

@Getter
public enum Tiers {

    /*
    ONLY the display variable will only be shown in game when utilizing the tier system.
    The display can be for instance "God", "Pro", "Immortal" etc...
     */

    ZERO(0, "0", 0),
    ONE(1,"I", 100),
    TWO(2, "II", 500),
    THREE(3, "III", 1000),
    FOUR(4, "IV", 1500),
    FIVE(5, "V", 3000),
    SIX(6, "VI", 5000),
    SEVEN(7, "VII", 10000),
    EIGHT(8, "VIII", 15000),
    NINE(9,"IX", 20000),
    TEN(10, "X", 30000),
    ELEVEN(11, "XI", 35000),
    TWELEVE(12, "XII", 40000),
    THIRTEEN(13, "XIII", 45000),
    FOURTEEN(14, "XIV", 50000),
    FIFTEEN(15, "XV", 55000),
    SIXTEEN(16, "XVI", 60000),
    SEVENTEEN(17, "XVII", 70000),
    EIGHTEEN(18, "XVIII", 80000),
    NINETEEN(19, "XIX", 90000),
    TWENTY(20, "XX", 100000);

    private static final Tiers[] VALUES = values();

    private final int tierLevel;
    private final String display;
    private final int requiredExperiences;

    Tiers(int tierLevel, String display, int requiredExperiences) {
        this.tierLevel = tierLevel;
        this.display = display;
        this.requiredExperiences = requiredExperiences;
    }

    public Tiers getNext() {
        int nextIndex = ordinal() + 1;
        return nextIndex >= VALUES.length ? null : VALUES[nextIndex];
    }

    public static Tiers getTierByNumber(int number) {
        for (Tiers tier : VALUES) {
            if (tier.tierLevel == number) {
                return tier;
            }
        }
        return ZERO;
    }

}
