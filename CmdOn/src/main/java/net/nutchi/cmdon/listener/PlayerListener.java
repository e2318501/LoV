package net.nutchi.cmdon.listener;

import lombok.RequiredArgsConstructor;
import net.nutchi.cmdon.CmdOn;
import net.nutchi.cmdon.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.function.Function;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private final CmdOn plugin;

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Function<String, String> replacer = s -> s.replace("%player%", event.getPlayer().getName());

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.execCmd(Event.LOGIN, replacer);

            if (event.getPlayer().hasPlayedBefore()) {
                plugin.execCmd(Event.NON_FIRST_LOGIN, replacer);
            } else {
                plugin.execCmd(Event.FIRST_LOGIN, replacer);
            }
        }, 1);
    }
}
