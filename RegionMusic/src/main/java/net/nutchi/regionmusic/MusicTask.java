package net.nutchi.regionmusic;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class MusicTask {
    private final Player player;
    private final Music music;
    private final BukkitTask task;

    public MusicTask(RegionMusic plugin, Player player, Music music) {
        this.player = player;
        this.music = music;
        if (music.isLoop()) {
            this.task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> player.playSound(player, music.getSound(), music.getVolume(), music.getPitch()), 0L, music.getDuration());
        } else {
            player.playSound(player, music.getSound(), music.getVolume(), music.getPitch());
            this.task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.stopSound(music.getSound()), music.getDuration());
        }
    }

    public void stopMusic() {
        player.stopSound(music.getSound());
        cancel();
    }

    public void cancel() {
        task.cancel();
    }

    public boolean isTarget(Player player) {
        return this.player.equals(player);
    }

    public boolean isTarget(Player player, Music music) {
        return this.player.equals(player) && this.music.equals(music);
    }
}
