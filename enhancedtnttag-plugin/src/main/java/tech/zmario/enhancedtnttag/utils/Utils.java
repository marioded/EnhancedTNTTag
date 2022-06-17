package tech.zmario.enhancedtnttag.utils;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import tech.zmario.enhancedtnttag.EnhancedTNTTag;
import tech.zmario.enhancedtnttag.api.manager.IArenaManager;
import tech.zmario.enhancedtnttag.api.objects.IArena;
import tech.zmario.enhancedtnttag.builders.ItemBuilder;
import tech.zmario.enhancedtnttag.enums.MessagesConfiguration;
import tech.zmario.enhancedtnttag.enums.SettingsConfiguration;
import tech.zmario.enhancedtnttag.objects.Placeholder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void sendItems(Player player, String type) {
        Set<String> keys = SettingsConfiguration.valueOf("ITEMS_" + type.toUpperCase()).getKeys();

        for (String key : keys) {
            String identifier = "items." + type + "." + key;
            FileConfiguration configuration = EnhancedTNTTag.getInstance().getConfig();

            String material = configuration.getString(identifier + ".material");

            ItemBuilder itemBuilder = new ItemBuilder(
                    new ItemStack(material.startsWith("skull:") ? Material.SKULL_ITEM : Material.valueOf(material.toUpperCase()),
                            (short) (material.startsWith("skull:") ? 3 : configuration.getInt(identifier + ".data"))));

            itemBuilder.setAmount(configuration.getInt(identifier + ".amount"));

            itemBuilder.setName(configuration.getString(identifier + ".name"));
            itemBuilder.setLore(configuration.getStringList(identifier + ".lore"));

            itemBuilder.setUnbreakable(configuration.getBoolean(identifier + ".unbreakable"));

            if (material.startsWith("skull:")) {
                itemBuilder.setPlayerSkull(material.replace("skull:", ""));
            }

            configuration.getStringList(identifier + ".enchants").forEach(string -> {
                String[] split = string.split(";");
                itemBuilder.addEnchant(getEnchantByName(split[0]), Integer.parseInt(split[1]));
            });

            configuration.getStringList(identifier + ".flags").forEach(string -> itemBuilder.addFlags(ItemFlag.valueOf(string.toUpperCase())));

            itemBuilder.setTag("TNTTag-Command", configuration.getString(identifier + ".command"));

            player.getInventory().setItem(configuration.getInt(identifier + ".slot"), itemBuilder.toItemStack());
        }
    }

    public static Enchantment getEnchantByName(String name) {
        switch (name.toLowerCase()) {
            case "prot":
            case "protection":
            case "protection_environmental":
                return Enchantment.PROTECTION_ENVIRONMENTAL;
            case "fireprot":
            case "protectionfire":
            case "fireprotection":
            case "protection_fire":
                return Enchantment.PROTECTION_FIRE;
            case "protection_fall":
            case "fallprotection":
            case "fallprot":
            case "featherfalling":
            case "feather":
                return Enchantment.PROTECTION_FALL;
            case "protectionexplosions":
            case "explosionprot":
            case "explosionprotection":
            case "protection_explosions":
                return Enchantment.PROTECTION_EXPLOSIONS;
            case "protection_projectile":
            case "projectile_prot":
            case "projectile":
            case "protectionprojectile":
            case "projectiles":
                return Enchantment.PROTECTION_PROJECTILE;
            case "oxygen":
            case "respiration":
            case "resp":
                return Enchantment.OXYGEN;
            case "waterworker":
            case "waterwork":
            case "water":
            case "water_worker":
            case "aqua":
            case "affinity":
            case "aquaaffinity":
                return Enchantment.WATER_WORKER;
            case "thorns":
                return Enchantment.THORNS;
            case "depth":
            case "depth_strider":
            case "depthstrider":
                return Enchantment.DEPTH_STRIDER;
            case "sharpness":
            case "sharp":
            case "damageall":
            case "damage":
            case "damage_all":
                return Enchantment.DAMAGE_ALL;
            case "damage_undead":
            case "damageundead":
            case "undead":
            case "smite":
                return Enchantment.DAMAGE_UNDEAD;
            case "damage_arthropods":
            case "arthropods":
                return Enchantment.DAMAGE_ARTHROPODS;
            case "kb":
            case "knockback":
                return Enchantment.KNOCKBACK;
            case "fireaspect":
            case "fire":
            case "fire_aspect":
                return Enchantment.FIRE_ASPECT;
            case "looting":
            case "loot":
            case "bonus":
            case "loot_bonus_mobs":
            case "lootbonusmobs":
                return Enchantment.LOOT_BONUS_MOBS;
            case "digspeed":
            case "dig":
            case "dig_speed":
            case "eff":
            case "efficiency":
                return Enchantment.DIG_SPEED;
            case "silk":
            case "touch":
            case "silk_touch":
            case "silktouch":
                return Enchantment.SILK_TOUCH;
            case "unb":
            case "unbreaking":
            case "durability":
                return Enchantment.DURABILITY;
            case "fortune":
            case "fort":
            case "lootbonusblocks":
                return Enchantment.LOOT_BONUS_BLOCKS;
            case "power":
            case "arrowdamage":
            case "arrow_damage":
                return Enchantment.ARROW_DAMAGE;
            case "punch":
            case "arrow_knockback":
            case "arrowknockback":
                return Enchantment.ARROW_KNOCKBACK;
            case "flame":
            case "arrowflame":
            case "arrowfire":
            case "arrow_fire":
            case "arrow_flame":
                return Enchantment.ARROW_FIRE;
            case "infinity":
            case "inf":
            case "arrowinfinity":
            case "arrow_infinity":
            case "arrowinf":
                return Enchantment.ARROW_INFINITE;
            case "luck":
            case "sea":
            case "luckofthesea":
            case "luckofsea":
                return Enchantment.LUCK;
            case "lure":
                return Enchantment.LURE;

        }
        return Enchantment.getByName(name);
    }
}
