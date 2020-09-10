package io.github.thebusybiscuit.slimefun4.implementation.items.androids;

import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.cscorelib2.item.CustomItem;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;

/**
 * This enum covers all different fuel sources a {@link ProgrammableAndroid} can have.
 * 
 * @author TheBusyBiscuit
 *
 */
public enum AndroidFuelSource {

    /**
     * This {@link ProgrammableAndroid} runs on solid fuel, e.g. Wood or coal
     */
    SOLID("", "&f這機器人需要固態燃料", "&f例如煤炭, 木頭, 等等..."),

    /**
     * This {@link ProgrammableAndroid} runs on liquid fuel, e.g. Fuel, Oil or Lava
     */
    LIQUID("", "&f這機器人需要液態燃料", "&f例如岩漿, 石油, 燃油, 等等..."),

    /**
     * This {@link ProgrammableAndroid} runs on nuclear fuel, e.g. Uranium
     */
    NUCLEAR("", "&f這機器人需要", "&f例如 鈾, 錼或高純度鈾");

    private final String[] lore;

    AndroidFuelSource(String... lore) {
        this.lore = lore;
    }

    /**
     * This returns a display {@link ItemStack} for this {@link AndroidFuelSource}.
     * 
     * @return An {@link ItemStack} to display
     */
    public ItemStack getItem() {
        return new CustomItem(SlimefunItems.COAL_GENERATOR, "&8\u21E9 &c燃料輸入口 &8\u21E9", lore);
    }

}
