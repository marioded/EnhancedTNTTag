package tech.zmario.enhancedtnttag.api.objects;

import lombok.Data;

import java.util.UUID;

@Data
public class GamePlayer {

    private final UUID uuid;
    private int wins;

    public void addWin() {
        wins += 1;
    }
}
