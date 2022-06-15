package tech.zmario.enhancedtnttag.storage;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class LocalStorage {

    private final List<UUID> buildPlayers = Lists.newArrayList();

}
