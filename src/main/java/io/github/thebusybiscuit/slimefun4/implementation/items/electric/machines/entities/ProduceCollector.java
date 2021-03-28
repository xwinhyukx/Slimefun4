package io.github.thebusybiscuit.slimefun4.implementation.items.electric.machines.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.cscorelib2.inventory.InvUtils;
import io.github.thebusybiscuit.cscorelib2.item.CustomItem;
import io.github.thebusybiscuit.slimefun4.api.items.ItemSetting;
import io.github.thebusybiscuit.slimefun4.api.items.settings.IntRangeSetting;
import io.github.thebusybiscuit.slimefun4.core.attributes.RecipeDisplayItem;
import io.github.thebusybiscuit.slimefun4.utils.SlimefunUtils;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;

/**
 * The {@link ProduceCollector} allows you to collect produce from animals.
 * Providing it with a bucket and a nearby {@link Cow} will allow you to obtain milk.
 * 
 * @author TheBusyBiscuit
 *
 */
public class ProduceCollector extends AContainer implements RecipeDisplayItem {

    private final Set<AnimalProduce> animalProduces = new HashSet<>();

    private final ItemSetting<Integer> range = new IntRangeSetting(this, "range", 1, 2, 32);

    @ParametersAreNonnullByDefault
    public ProduceCollector(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(category, item, recipeType, recipe);

        addItemSetting(range);
    }

    @Override
    protected void registerDefaultRecipes() {
        addProduce(new AnimalProduce(new ItemStack(Material.BUCKET), Cow.class::isInstance, new ItemStack(Material.MILK_BUCKET)));
        addProduce(new AnimalProduce(new ItemStack(Material.BOWL), MushroomCow.class::isInstance, new ItemStack(Material.MUSHROOM_STEW)));
    }

    /**
     * This method adds a new {@link AnimalProduce} to this machine.
     * 
     * @param produce
     *            The {@link AnimalProduce} to add
     */
    public void addProduce(@Nonnull AnimalProduce produce) {
        Validate.notNull(produce, "A produce cannot be null");

        this.animalProduces.add(produce);
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockTicker() {

            @Override
            public void tick(Block b, SlimefunItem sf, Config data) {
                ProduceCollector.this.tick(b);
            }

            @Override
            public boolean isSynchronized() {
                // We override the preRegister() method to override the sync setting here
                return true;
            }
        });
    }

    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> displayRecipes = new ArrayList<>();

        displayRecipes.add(new CustomItem(Material.BUCKET, null, "&f需要&b牛&f在附近"));
        displayRecipes.add(new ItemStack(Material.MILK_BUCKET));

        displayRecipes.add(new CustomItem(Material.BOWL, null, "&f需要&b哞菇&f在附近"));
        displayRecipes.add(new ItemStack(Material.MUSHROOM_STEW));

        return displayRecipes;
    }

    @Override
    protected MachineRecipe findNextRecipe(@Nonnull BlockMenu inv) {
        for (int slot : getInputSlots()) {
            for (AnimalProduce produce : animalProduces) {
                ItemStack item = inv.getItemInSlot(slot);

                if (!SlimefunUtils.isItemSimilar(item, produce.getInput()[0], true) || !InvUtils.fits(inv.toInventory(), produce.getOutput()[0], getOutputSlots())) {
                    continue;
                }

                if (isAnimalNearby(inv.getBlock(), produce::test)) {
                    inv.consumeItem(slot);
                    return produce;
                }
            }
        }

        return null;
    }

    @ParametersAreNonnullByDefault
    private boolean isAnimalNearby(Block b, Predicate<LivingEntity> predicate) {
        int radius = range.getValue();
        return !b.getWorld().getNearbyEntities(b.getLocation(), radius, radius, radius, n -> isValidAnimal(n, predicate)).isEmpty();
    }

    @ParametersAreNonnullByDefault
    private boolean isValidAnimal(Entity n, Predicate<LivingEntity> predicate) {
        if (n instanceof LivingEntity) {
            if (n instanceof Ageable && !((Ageable) n).isAdult()) {
                // We only take adults into consideration
                return false;
            } else {
                return predicate.test((LivingEntity) n);
            }
        } else {
            return false;
        }
    }

    @Override
    public String getMachineIdentifier() {
        return "PRODUCE_COLLECTOR";
    }

    @Override
    public ItemStack getProgressBar() {
        return new ItemStack(Material.SHEARS);
    }

}
