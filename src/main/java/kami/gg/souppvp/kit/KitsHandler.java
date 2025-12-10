package kami.gg.souppvp.kit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.inherit.*;
import kami.gg.souppvp.kit.kits.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class KitsHandler {

    @Getter public List<Kit> kits;

    public KitsHandler(){
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

        registerKitListeners();
    }

    public void registerKitListeners(){
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new DefaultKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new ChemistKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new KangarooKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new FishermanKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new AvatarKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new FiremanKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new CactusKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new VampireKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new EskimoKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new StomperKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new SwitcherooKit(), SoupPvP.getInstance());
        //SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new WitherKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new BarbarianKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new NinjaKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new TorchKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new ZeusKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new RogueKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new EnhancerKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new SpidermanKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new ProKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new ArcherKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new GrapplerKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new PalioxisKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new MelonKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new CyclopsKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new ViperKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new PhantomKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new CopyCatKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new DwarfKit(), SoupPvP.getInstance());
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new YodaKit(), SoupPvP.getInstance());
    }

    public Kit getKitByName(String name){
        for (Kit kit : getKits()){
            if (kit.getName().equalsIgnoreCase(name)){
                return kit;
            }
        }
        return null;
    }

    public void addKit(Kit kit){
        getKits().add(kit);
    }

    public void removeKit(Kit kit){
        getKits().remove(kit);
    }

}
