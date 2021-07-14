/*
 * MIT License
 *
 * Copyright (c) 2021 dkim19375
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.dkim19375.itemmovedetectionlib.event;

import me.dkim19375.itemmovedetectionlib.util.TransferType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * An event fired when a player moves or drops an item from an inventory
 */
public class InventoryItemTransferEvent extends InventoryEvent implements Cancellable {
    @NotNull
    private final Player player = (Player) transaction.getPlayer();
    @NotNull
    private final TransferType type;
    @NotNull
    private final List<ItemStack> items;
    @NotNull
    private final Inventory from;
    @Nullable
    private final Inventory to;
    private boolean cancelled;
    @Nullable
    private final InventoryAction inventoryAction;

    public InventoryItemTransferEvent(@NotNull TransferType type, InventoryView transaction, @NotNull List<ItemStack> items,
                                      @NotNull Inventory from, @Nullable Inventory to, boolean cancelled, @Nullable InventoryAction inventoryAction) {
        super(transaction);
        this.type = type;
        this.items = Collections.unmodifiableList(items);
        this.from = from;
        this.to = to;
        this.cancelled = cancelled;
        this.inventoryAction = inventoryAction;
    }

    /**
     * Returns the player involved in this event
     *
     * @return Player who is involved in this event
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * @return an immutable list of items involved in this event
     */
    @NotNull
    public List<ItemStack> getItems() {
        return items;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * @return the type of item transfer
     */
    @NotNull
    public TransferType getType() {
        return type;
    }

    /**
     * @return the inventory the item was moved from
     */
    @NotNull
    public Inventory getFrom() {
        return from;
    }

    /**
     * @return the inventory the item was moved to, null if dropped
     */
    @Nullable
    public Inventory getTo() {
        return to;
    }

    /**
     * @return the {@link InventoryAction} of the
     */
    @Nullable
    public InventoryAction getInventoryAction() {
        return inventoryAction;
    }
}