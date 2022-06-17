package tech.zmario.enhancedtnttag.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import tech.zmario.enhancedtnttag.api.objects.GamePlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class LocalStorage {

    private final List<UUID> buildPlayers = Lists.newArrayList();
    private final Map<UUID, GamePlayer> gamePlayers = Maps.newHashMap();

}
