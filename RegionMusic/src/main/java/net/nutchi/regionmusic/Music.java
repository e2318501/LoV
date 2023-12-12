package net.nutchi.regionmusic;

import lombok.Data;

@Data
public class Music {
    private final String name;
    private final boolean enabled;
    private final String region;
    private final String sound;
    private final float volume;
    private final float pitch;
    private final boolean loop;
    private final long duration;
    private final int priority;
}
