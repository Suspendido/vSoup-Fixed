package kami.gg.souppvp.handlers;

import kami.gg.souppvp.coinflip.CoinFlip;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class CoinFlipsHandler {

    private final List<CoinFlip> coinFlips;

    public CoinFlipsHandler() {
        this.coinFlips = new ArrayList<>();
    }

    public void addNewCoinFlip(CoinFlip coinFlip) {
        coinFlips.add(coinFlip);
    }

    public void removeCoinFlip(CoinFlip coinFlip) {
        coinFlips.remove(coinFlip);
    }

    public Boolean hasCoinFlipWager(UUID uuid) {
        for (CoinFlip coinFlip : coinFlips) {
            if (coinFlip.getCreator().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public CoinFlip getPlayerCoinFlip(UUID uuid) {
        for (CoinFlip coinFlip : coinFlips) {
            if (coinFlip.getCreator().equals(uuid)) {
                return coinFlip;
            }
        }
        return null;
    }

}
