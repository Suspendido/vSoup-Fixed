package kami.gg.souppvp.kit.ability;

import kami.gg.souppvp.kit.ability.impl.*;

import java.util.HashMap;
import java.util.Map;

public class KitAbilityRegistry {

    private final Map<String, KitAbility> abilities;

    public KitAbilityRegistry() {
        this.abilities = new HashMap<>();
        registerDefaultAbilities();
    }

    private void registerDefaultAbilities() {
        registerAbility(new NoneAbility());
        registerAbility(new ZeusAbility());
        registerAbility(new ArcherAbility());
        registerAbility(new FishermanAbility());
        registerAbility(new AvatarAbility());
        registerAbility(new BarbarianAbility());
        registerAbility(new CactusAbility());
        registerAbility(new ChemistAbility());
        registerAbility(new CopyCatAbility());
        registerAbility(new CyclopsAbility());
        registerAbility(new DwarfAbility());
        registerAbility(new EskimoAbility());
        registerAbility(new EnhancerAbility());
        registerAbility(new FiremanAbility());
        registerAbility(new GrapplerAbility());
        registerAbility(new KangarooAbility());
        registerAbility(new MelonAbility());
        registerAbility(new NinjaAbility());
        registerAbility(new PalioxisAbility());
        registerAbility(new PhantomAbility());
        registerAbility(new ProAbility());
        registerAbility(new RogueAbility());
        registerAbility(new SpidermanAbility());
        registerAbility(new StomperAbility());
        registerAbility(new SwitcherooAbility());
        registerAbility(new TorchAbility());
        registerAbility(new VampireAbility());
        registerAbility(new ViperAbility());
        registerAbility(new YodaAbility());
    }

    public void registerAbility(KitAbility ability) {
        abilities.put(ability.getName(), ability);
    }

    public KitAbility getAbility(String name) {
        return abilities.get(name);
    }

    public Map<String, KitAbility> getAbilities() {
        return new HashMap<>(abilities);
    }
}
