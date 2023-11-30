package net.nutchi.regionmusic;

import lombok.RequiredArgsConstructor;
import net.raidstone.wgevents.WorldGuardEvents;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MusicManager {
    private final RegionMusic plugin;
    private final List<Music> musicList = new ArrayList<>();
    private final List<MusicTask> musicTaskList = new ArrayList<>();
    private final List<Player> mutePlayerList = new ArrayList<>();

    public void makeMute(Player player) {
        mutePlayerList.add(player);
        stopMusic(player);
    }

    public void makeUnmute(Player player) {
        mutePlayerList.remove(player);
        WorldGuardEvents.getRegionsNames(player.getUniqueId()).forEach(r -> startMusic(player, r));
    }

    public List<String> getMutePlayerNames() {
        return mutePlayerList.stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    public void clearPlayer(Player player) {
        mutePlayerList.remove(player);
        clearTask(player);
    }

    public void startMusic(Player player, String region) {
        if (!mutePlayerList.contains(player)) {
            musicList.stream()
                    .filter(Music::isEnabled)
                    .filter(m -> m.getRegion().equalsIgnoreCase(region))
                    .forEach(m -> musicTaskList.add(new MusicTask(plugin, player, m)));
        }
    }

    public void stopMusic(Player player) {
        musicTaskList.stream()
                .filter(t -> t.isTarget(player))
                .forEach(MusicTask::stopMusic);
        musicTaskList.removeIf(t -> t.isTarget(player));
    }

    public void stopMusic(Player player, String region) {
        musicTaskList.stream()
                .filter(t -> t.isTarget(player, region))
                .forEach(MusicTask::stopMusic);
        musicTaskList.removeIf(t -> t.isTarget(player, region));
    }

    public void loadMusic() {
        clearAllTasks();
        musicList.clear();

        plugin.getConfig().getMapList("music").forEach(e -> {
            String name = (String) e.get("name");
            boolean enabled = (boolean) e.get("enabled");
            String region = (String) e.get("region");
            String sound = (String) e.get("sound");
            float volume = (float) (double) e.get("volume");
            float pitch = (float) (double) e.get("pitch");
            boolean loop = (boolean) e.get("loop");
            long duration = (long) (int) e.get("duration");

            musicList.add(new Music(name, enabled, region, sound, volume, pitch, loop, duration));
        });
    }

    private void clearTask(Player player) {
        musicTaskList.stream()
                .filter(t -> t.isTarget(player))
                .forEach(MusicTask::cancel);
        musicTaskList.removeIf(t -> t.isTarget(player));
    }

    private void clearAllTasks() {
        musicTaskList.forEach(MusicTask::cancel);
        musicTaskList.clear();
    }
}
