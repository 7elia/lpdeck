package me.elia.lpdeck.spotify;

import lombok.Data;

@Data
public class SpotifyState {
    private final boolean playing;
    private final boolean shuffle;
    private final boolean repeat;
}
