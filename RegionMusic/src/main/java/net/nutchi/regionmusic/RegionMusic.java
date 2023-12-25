package net.nutchi.regionmusic;

import lombok.Getter;
import net.nutchi.regionmusic.listener.PlayerListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public final class RegionMusic extends JavaPlugin {
    private final MusicManager musicManager = new MusicManager(this);

    @Override
    public void onEnable() {
        saveDefaultConfig();

        musicManager.loadMusic();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 1) {
            switch (args[0]) {
                case "reload":
                    reloadConfig();
                    musicManager.loadMusic();
                    sender.sendMessage("Reloaded music list");
                    return true;
                case "mute":
                    if (args.length == 2) {
                        getPlayer(args[1]).ifPresent(musicManager::makeMute);
                        return true;
                    } else {
                        return false;
                    }
                case "unmute":
                    if (args.length == 2) {
                        getPlayer(args[1]).ifPresent(musicManager::makeUnmute);
                        return true;
                    } else {
                        return false;
                    }
                case "mutelist":
                    sender.sendMessage("Mute player list: " + String.join(", ", musicManager.getMutePlayerNames()));
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("reload", "mute", "unmute", "mutelist").filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        } else if (args.length == 2){
            switch (args[0]) {
                case "mute":
                    return getServer().getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
                case "unmute":
                    return musicManager.getMutePlayerNames().stream().filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
                default:
                    return Collections.emptyList();
            }
        }else {
            return Collections.emptyList();
        }
    }

    public Optional<Player> getPlayer(String name) {
        return Optional.ofNullable(getServer().getPlayer(name));
    }
}
