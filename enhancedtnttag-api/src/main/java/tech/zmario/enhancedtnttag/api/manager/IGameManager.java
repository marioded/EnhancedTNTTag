package tech.zmario.enhancedtnttag.api.manager;

import tech.zmario.enhancedtnttag.api.objects.IArena;

public interface IGameManager {

    void startGame(IArena arena);

    void endGame(IArena arena);

}
