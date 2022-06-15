package tech.zmario.enhancedtnttag.utils;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import tech.zmario.enhancedtnttag.api.manager.IArenaManager;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.api.objects.Placeholder;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class Utils {

    @Getter
    private final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    @Getter
    private final Location mainLobby = SettingsConfiguration.MAIN_LOBBY_LOCATION.getString() != null ? deserializeLocation(SettingsConfiguration.MAIN_LOBBY_LOCATION.getString()) : deserializeLocation("world;0;100;0;90;90");

    public String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public void spawnFireworks(Location location, int power, FireworkEffect.Type type) {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        fireworkMeta.setPower(power);
        fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).with(type).build());

        firework.setFireworkMeta(fireworkMeta);

        firework.detonate();
    }

    public Location deserializeLocation(String location) {
        String[] split = location.split(";");

        if (split.length == 4) {
            return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
        }

        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }

    public Object getPrivateField(Object instance, String fieldName) throws Exception {
        return getPrivateField(instance.getClass(), instance, fieldName);
    }

    public Object getPrivateField(Class<?> clazz, Object instance, String fieldName) throws Exception {
        Field f = clazz.getDeclaredField(fieldName);
        f.setAccessible(true);

        return f.get(instance);
    }

    public String createTaggedPlayersMessage(IArena arena) {
        Iterator<UUID> taggers = arena.getTaggers().iterator();
        StringBuilder sb = new StringBuilder();

        while (taggers.hasNext()) {
            UUID uuid = taggers.next();
            Player player = Bukkit.getPlayer(uuid);

            sb.append(MessagesConfiguration.PLACEHOLDERS_PLAYER_LIST_PLAYER.getString(player, new Placeholder("%player%", player.getName())));

            if (taggers.hasNext()) {
                sb.append(MessagesConfiguration.PLACEHOLDERS_PLAYER_LIST_COMMA.getString(player));
            } else {
                sb.append(MessagesConfiguration.PLACEHOLDERS_PLAYER_LIST_DOT.getString(player));
            }
        }

        return sb.toString();
    }

    public void playSound(String path, Player player) {
        if (path.equalsIgnoreCase("none") || path.isEmpty()) return;

        String[] split = path.split(";");

        Sound sound = Sound.valueOf(split[0]);
        float volume = 0;
        float pitch = 0;

        if (split[1] != null) {
            volume = Float.parseFloat(split[1]);
        }
        if (split[2] != null) {
            pitch = Float.parseFloat(split[2]);
        }

        player.playSound(player.getLocation(), sound, volume, pitch);
    }
    public void sendTitle(String path, Player player) {
        if (path.equalsIgnoreCase("none") || path.isEmpty()) return;

        String[] split = path.split(";");

        String title = colorize(split[0]);
        String subtitle = colorize(split[1]);

        int fadeIn = 0;
        int stay = 20;
        int fadeOut = 0;

        if (split[2] != null) {
            fadeIn = Integer.parseInt(split[2]);
        }
        if (split[3] != null) {
            stay = Integer.parseInt(split[3]);
        }
        if (split[4] != null) {
            fadeOut = Integer.parseInt(split[4]);
        }

        try {
            Class<?> PacketPlayOutTitleClass = getNMSClass("PacketPlayOutTitle");
            Class<?> IChatBaseComponentClass = getNMSClass("IChatBaseComponent");

            Constructor<?> constructor = PacketPlayOutTitleClass.getConstructor(PacketPlayOutTitleClass
                    .getDeclaredClasses()[0], IChatBaseComponentClass, int.class, int.class, int.class);

            Object titleComponent = IChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\":\"" + title + "\"}");
            Object subTitleComponent = IChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\":\"" + subtitle + "\"}");

            Object titlePacket = constructor.newInstance(PacketPlayOutTitleClass
                    .getDeclaredClasses()[0].getField("TITLE").get(null), titleComponent, fadeIn, stay, fadeOut);
            Object subTitlePacket = constructor.newInstance(PacketPlayOutTitleClass
                    .getDeclaredClasses()[0].getField("SUBTITLE")
                    .get(null), subTitleComponent, fadeIn, stay, fadeOut);

            sendPacket(player, titlePacket);
            sendPacket(player, subTitlePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hidePlayers(Player player, IArenaManager arenaManager) {
        for (Player online : Bukkit.getOnlinePlayers()) {

            if (player == online) continue;

            Optional<IArena> playerArena = arenaManager.getArena(player);
            Optional<IArena> targetArena = arenaManager.getArena(online);

            player.hidePlayer(online);
            online.hidePlayer(player);

            if (playerArena.isPresent() && targetArena.isPresent()) {
                if (targetArena.equals(playerArena)) {
                    IArena arena = playerArena.get();

                    // Players are both spectators
                    if (arena.getSpectators().contains(player.getUniqueId())) {
                        if (arena.getSpectators().contains(online.getUniqueId())) {
                            player.showPlayer(online);
                            online.showPlayer(player);
                        }

                        if (arena.getPlayers().contains(online.getUniqueId())) {
                            player.showPlayer(online);
                        }

                        continue;
                    }

                    if (arena.getPlayers().contains(online.getUniqueId())) {
                        online.showPlayer(player);
                        player.showPlayer(online);
                    }
                }
            } else if (!playerArena.isPresent() && !targetArena.isPresent()) {
                online.showPlayer(player);
                player.showPlayer(online);
            }
        }
    }

    public Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void sendPacket(Player player, Object packet) {
        Object playerHandle;
        Object playerConnection;
        try {
            playerHandle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
            playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);

            if (version.contains("18")) {
                playerConnection.getClass().getMethod("a", new Class[]{getNMSClass("Packet")})
                        .invoke(playerConnection, packet);
            } else {
                playerConnection.getClass().getMethod("sendPacket", new Class[]{getNMSClass("Packet")})
                        .invoke(playerConnection, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String serializeLocation(Location location) {
        return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
