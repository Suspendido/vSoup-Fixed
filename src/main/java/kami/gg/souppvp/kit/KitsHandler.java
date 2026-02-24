package kami.gg.souppvp.kit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.inherit.*;
import kami.gg.souppvp.kit.kits.*;
import kami.gg.souppvp.profile.Profile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class KitsHandler {

    @Getter public List<Kit> kits;

    public KitsHandler() {
        kits = new ArrayList<>();  //A B C D E F G H I J K L M N O P Q R S T U V W X Y Z

        addKit(new ArcherKit());
        addKit(new AvatarKit());
        addKit(new BarbarianKit());
        addKit(new CactusKit());
        addKit(new ChemistKit());
        addKit(new CopyCatKit());
        addKit(new CyclopsKit());
        addKit(new DefaultKit());
        addKit(new DwarfKit());
        addKit(new EnhancerKit());
        addKit(new EskimoKit());
        addKit(new FiremanKit());
        addKit(new FishermanKit());
        addKit(new GrapplerKit());
        addKit(new KangarooKit());
        addKit(new MelonKit());
        addKit(new NinjaKit());
        addKit(new PalioxisKit());
        addKit(new PhantomKit());
        addKit(new ProKit());
        addKit(new RogueKit());
        addKit(new SpidermanKit());
        addKit(new StomperKit());
        addKit(new SwitcherooKit());
        addKit(new TorchKit());
        addKit(new VampireKit());
        addKit(new ViperKit());
        addKit(new YodaKit());
        addKit(new ZeusKit());

    }

    public Kit getKitByName(String name) {
        for (Kit kit : getKits()){
            if (kit.getName().equalsIgnoreCase(name)){
                return kit;
            }
        }
        return null;
    }

    public void addKit(Kit kit) {
        getKits().add(kit);
    }

    public boolean hasKitUnlocked(Profile profile, Kit kit) {
        if (kit == null) return false;
        if (SoupPvP.getIsFreeKitsMode()) return true;
        return profile.getUnlockedKits().contains(kit.getName());
    }
}
