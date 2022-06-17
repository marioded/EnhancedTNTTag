package tech.zmario.enhancedtnttag.builders;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import tech.zmario.enhancedtnttag.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder setName(String name) {
        itemMeta.setDisplayName(Utils.colorize(name));
        return this;
    }

    public void setAmount(int amount) {
        itemStack.setAmount(amount);
    }

    public void setLore(List<String> lore) {
        itemMeta.setLore(lore.stream().map(Utils::colorize).collect(Collectors.toList()));
    }

    public void addFlags(ItemFlag... flags) {
        itemMeta.addItemFlags(flags);
    }

    public void addEnchant(Enchantment enchantment, int level) {
        itemMeta.addEnchant(enchantment, level, true);
    }

    public void setPlayerSkull(String playerName) {
        if (itemStack.getType() != Material.SKULL_ITEM && itemStack.getDurability() != 3) return;
        SkullMeta skullMeta = (SkullMeta) itemMeta;
        skullMeta.setOwner(playerName);
        itemStack.setItemMeta(skullMeta);
    }

    public ItemStack toItemStack() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public void setUnbreakable(boolean value) {
        itemMeta.spigot().setUnbreakable(value);
    }

    public void setTag(String key, String value) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString(key, value);
    }
}
