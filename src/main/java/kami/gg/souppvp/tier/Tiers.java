package kami.gg.souppvp.tier;

import lombok.Getter;

@Getter
public enum Tiers {

    /*
    ONLY the display variable will only be shown in game when utilizing the tier system.
    The display can be for instance "God", "Pro", "Immortal" etc...
     */

    ZERO(0, "0", 0, 0),
    ONE(1,"I", 100, 500),
    TWO(2, "II", 500, 1000),
    THREE(3, "III", 1000, 1000),
    FOUR(4, "IV", 1500, 1000),
    FIVE(5, "V", 3000, 1000),
    SIX(6, "VI", 5000, 1000),
    SEVEN(7, "VII", 10000, 1000),
    EIGHT(8, "VIII", 15000, 1000),
    NINE(9,"IX", 20000, 1000),
    TEN(10, "X", 30000, 1000),
    ELEVEN(11, "XI", 35000, 1000),
    TWELEVE(12, "XII", 40000, 1000),
    THIRTEEN(13, "XIII", 45000, 1000),
    FOURTEEN(14, "XIV", 50000, 1000),
    FIFTEEN(15, "XV", 55000, 2000),
    SIXTEEN(16, "XVI", 60000, 2000),
    SEVENTEEN(17, "XVII", 70000, 2000),
    EIGHTEEN(18, "XVIII", 80000, 2000),
    NINETEEN(19, "XIX", 90000, 2000),
    TWENTY(20, "XX", 100000, 3000);

    private static final Tiers[] VALUES = values();

    private final int tierLevel;
    private final String display;
    private final int requiredExperiences;
    private final int creditsReward;

    Tiers(int tierLevel, String display, int requiredExperiences, int creditsReward) {
        this.tierLevel = tierLevel;
        this.display = display;
        this.requiredExperiences = requiredExperiences;
        this.creditsReward = creditsReward;
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
