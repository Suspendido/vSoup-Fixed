package kami.gg.souppvp.feats.hooks.clients.type;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.combat.CombatModule;
import com.lunarclient.apollo.module.nametag.Nametag;
import com.lunarclient.apollo.module.nametag.NametagModule;
import kami.gg.souppvp.feats.hooks.clients.Client;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LunarClient implements Client {

    private final NametagModule nametagModule;
    private final CombatModule combatModule;

    public LunarClient() {
        this.nametagModule = Apollo.getModuleManager().getModule(NametagModule.class);
        this.combatModule = Apollo.getModuleManager().getModule(CombatModule.class);
    }

    @Override
    public void overrideNametags(Player target, Player viewer, List<String> tag) {
        Apollo.getPlayerManager().getPlayer(viewer.getUniqueId()).ifPresent(apolloPlayer -> {
            List<Component> components = tag.stream().map(Component::text).collect(Collectors.toList());
            Collections.reverse(components);
            nametagModule.overrideNametag(apolloPlayer, target.getUniqueId(), Nametag.builder().lines(components).build());
        });
    }

    @Override
    public void clearNametags(Player player) {
        Apollo.getPlayerManager().getPlayer(player.getUniqueId()).ifPresent(nametagModule::resetNametags);
    }


    @Override
    public void handleJoin(Player player) {
        Apollo.getPlayerManager().getPlayer(player.getUniqueId()).ifPresent(apolloPlayer -> combatModule.getOptions().set(apolloPlayer, CombatModule.DISABLE_MISS_PENALTY, true));
    }
}
