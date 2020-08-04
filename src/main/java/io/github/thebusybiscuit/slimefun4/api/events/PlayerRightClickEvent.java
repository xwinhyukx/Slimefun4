package io.github.thebusybiscuit.slimefun4.api.events;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.cscorelib2.data.ComputedOptional;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

public class PlayerRightClickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final PlayerInteractEvent event;
    private final Player player;

    private final Optional<ItemStack> itemStack;
    private final Optional<Block> clickedBlock;

    private final EquipmentSlot hand;
    private final BlockFace face;

    private ComputedOptional<SlimefunItem> slimefunItem = ComputedOptional.createNew();
    private ComputedOptional<SlimefunItem> slimefunBlock = ComputedOptional.createNew();

    private Result itemResult = Result.DEFAULT;
    private Result blockResult = Result.DEFAULT;

    public PlayerRightClickEvent(PlayerInteractEvent e) {
        event = e;
        player = e.getPlayer();
        clickedBlock = Optional.ofNullable(e.getClickedBlock());
        face = e.getBlockFace();
        hand = e.getHand();

        if (e.getItem() == null || e.getItem().getType() == Material.AIR || e.getItem().getAmount() == 0) {
            itemStack = Optional.empty();
        }
        else {
            itemStack = Optional.of(e.getItem());
        }
    }

    public PlayerInteractEvent getInteractEvent() {
        return event;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * This method returns the {@link ItemStack} that was held in the hand of the {@link Player}.
     * It will never return null, should there be no {@link ItemStack} then it will return
     * {@code new ItemStack(Material.AIR)}.
     * 
     * @return The {@link ItemStack} that the {@link Player} right clicked with
     */
    public ItemStack getItem() {
        return itemStack.orElse(new ItemStack(Material.AIR));
    }

    /**
     * This returns the hand that was used in this interaction.
     * Can either be {@code EquipmentSlot.HAND} or {@code EquipmentSlot.OFF_HAND}.
     * 
     * @return The hand used in this {@link Event}
     */
    public EquipmentSlot getHand() {
        return hand;
    }

    public Optional<Block> getClickedBlock() {
        return clickedBlock;
    }

    public BlockFace getClickedFace() {
        return face;
    }

    public Optional<SlimefunItem> getSlimefunItem() {

        if (!slimefunItem.isComputed()) {
            if (itemStack.isPresent()) {
                slimefunItem.compute(SlimefunItem.getByItem(itemStack.get()));
            }
            else {
                slimefunItem = ComputedOptional.empty();
            }
        }

        return slimefunItem.getAsOptional();
    }

    public Optional<SlimefunItem> getSlimefunBlock() {

        if (!slimefunBlock.isComputed()) {
            if (clickedBlock.isPresent()) {
                slimefunBlock.compute(BlockStorage.check(clickedBlock.get()));
            }
            else {
                slimefunBlock = ComputedOptional.empty();
            }
        }

        return slimefunBlock.getAsOptional();
    }

    public void cancel() {
        itemResult = Result.DENY;
        blockResult = Result.DENY;
    }

    public Result useItem() {
        return itemResult;
    }

    public Result useBlock() {
        return blockResult;
    }

    public void setUseItem(Result result) {
        itemResult = result;
    }

    public void setUseBlock(Result result) {
        blockResult = result;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

}
