package tech.zmario.enhancedtnttag.api;

import tech.zmario.enhancedtnttag.api.commands.interfaces.SubCommand;
import tech.zmario.enhancedtnttag.api.manager.IArenaManager;
import tech.zmario.enhancedtnttag.api.manager.IGameManager;
import tech.zmario.enhancedtnttag.api.manager.ILeaderBoardManager;
import tech.zmario.enhancedtnttag.api.manager.ISetupManager;

public interface EnhancedTNTTagAPI {

    IArenaManager getArenaManager();

    IGameManager getGameManager();

    ISetupManager getSetupManager();

    ILeaderBoardManager getLeaderBoardManager();

    void registerSubCommand(String name, SubCommand subCommand);

}
