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

package me.dkim19375.itemmovedetectionlib.listener;

import me.dkim19375.itemmovedetectionlib.event.InventoryItemTransferEvent;
import me.dkim19375.itemmovedetectionlib.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class ItemMoveListeners implements Listener {
    private final Map<UUID, PickedItems> pickedItems = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onDrop(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final PickedItems pickedItem = pickedItems.get(player.getUniqueId());
        if (pickedItem == null) {
            return;
        }
        final ItemStack droppedItem = event.getItemDrop().getItemStack().clone();
        final int newAmount = pickedItem.getItem().getAmount() - droppedItem.getAmount();
        if (newAmount <= 0) {
            pickedItems.remove(player.getUniqueId());
        } else {
            droppedItem.setAmount(newAmount);
            pickedItem.setItem(droppedItem);
        }
        final InventoryLoc pickedUpLoc = pickedItem.getLoc();
        final ItemStack item = pickedItem.getItem();
        final int rawSlot = pickedItem.getSlot();
        final InventoryView view = pickedItem.getView();
        final Inventory top = view.getTopInventory();
        final Inventory bottom = view.getBottomInventory();
        final boolean crafting = top.getType() == InventoryType.CRAFTING;

        final TransferType type = (pickedUpLoc == InventoryLoc.BOTTOM)
                ? TransferType.DROP_SELF : (crafting ? TransferType.DROP_CRAFTING : TransferType.DROP_OTHER);

        final boolean cancelled = activateEvent(type, item, (pickedUpLoc == InventoryLoc.BOTTOM) ? bottom : top, view);
        if (!cancelled) {
            return;
        }
        view.setItem(rawSlot, item.clone());
        event.getItemDrop().remove();
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onDrag(InventoryDragEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = event.getView().getTopInventory();
        final Inventory bottom = event.getView().getBottomInventory();
        final PickedItems pickedItem = pickedItems.getOrDefault(player.getUniqueId(), null);
        final InventoryLoc pickedUpLoc = pickedItem == null ? null : pickedItem.getLoc();

        final List<ItemStack> topItems = new ArrayList<>();
        final List<ItemStack> bottomItems = new ArrayList<>();
        for (Map.Entry<Integer, ItemStack> e : event.getNewItems().entrySet()) {
            if (e.getKey() < top.getSize()) {
                topItems.add(e.getValue());
                continue;
            }
            bottomItems.add(e.getValue());
        }
        if (pickedUpLoc == InventoryLoc.TOP && (!bottomItems.isEmpty())) {
            activateEvent(TransferType.PUT_OTHER_SELF, bottomItems, top, bottom, event);
        }
        if (pickedUpLoc == InventoryLoc.BOTTOM && (!topItems.isEmpty())) {
            activateEvent(TransferType.PUT_SELF_OTHER, bottomItems, bottom, top, event);
        }
        if (!event.isCancelled()) {
            pickedItems.remove(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final PickedItems pickedItem = pickedItems.getOrDefault(player.getUniqueId(), null);
        final InventoryLoc pickedUpLoc = pickedItem == null ? null : pickedItem.getLoc();
        final Inventory top = event.getView().getTopInventory();
        final Inventory bottom = event.getView().getBottomInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final InventoryAction action = event.getAction();
        final SimplifiedAction simplifiedAction = SimplifiedAction.fromAction(action);
        final ItemStack currentItem = event.getCurrentItem();
        final ItemStack cursorItem = event.getCursor();
        final boolean crafting = top.getType() == InventoryType.CRAFTING;
        final boolean inventoryIsPlayer = clickedInventory != null && (clickedInventory instanceof PlayerInventory ||
                clickedInventory.getType() == InventoryType.CRAFTING);
        final boolean clickedIsCrafting = crafting && top == clickedInventory;
        final InventoryLoc clickedInvLoc = inventoryIsPlayer
                ? (clickedIsCrafting ? InventoryLoc.TOP : InventoryLoc.BOTTOM)
                : InventoryLoc.TOP;
        boolean put = false;
        boolean remove = false;

        if (simplifiedAction == SimplifiedAction.NOTHING) {
            return;
        }
        if (clickedInventory == null) {
            if (simplifiedAction == SimplifiedAction.DROP_CURSOR) {
                activateEvent((pickedUpLoc == InventoryLoc.BOTTOM) ? TransferType.DROP_SELF : TransferType.DROP_OTHER, cursorItem,
                        (pickedUpLoc == InventoryLoc.BOTTOM) ? bottom : top,
                        null, event, action);
            }
            return;
        }
        if (simplifiedAction == SimplifiedAction.PICKUP) {
            put = true;
        }
        if (simplifiedAction != null) {
            switch (simplifiedAction) {
                case DROP_CURSOR: {
                    activateEvent(inventoryIsPlayer ? TransferType.DROP_SELF : TransferType.DROP_OTHER, cursorItem,
                            inventoryIsPlayer ? bottom : top,
                            null, event, action);
                    break;
                }
                case DROP_SLOT: {
                    activateEvent(inventoryIsPlayer ? TransferType.DROP_SELF : TransferType.DROP_OTHER, currentItem,
                            inventoryIsPlayer ? bottom : top,
                            null, event, action);
                    break;
                }
                case PLACE: {
                    remove = true;
                    if (pickedUpLoc == null) {
                        break;
                    }
                    if (pickedUpLoc == InventoryLoc.TOP) {
                        if (inventoryIsPlayer) {
                            activateEvent(TransferType.PUT_OTHER_SELF, cursorItem, top, bottom, event, action);
                        }
                        break;
                    }
                    if (!inventoryIsPlayer || crafting) {
                        activateEvent(TransferType.PUT_SELF_OTHER, cursorItem, bottom, top, event, action);
                    }
                    break;
                }
                case SWAP_WITH_CURSOR: {
                    put = true;
                    if (pickedUpLoc == null) {
                        break;
                    }
                    if (!inventoryIsPlayer && pickedUpLoc == InventoryLoc.TOP) {
                        activateEvent(TransferType.PUT_OTHER_SELF, cursorItem, top, bottom, event, action);
                    } else if (inventoryIsPlayer && pickedUpLoc == InventoryLoc.BOTTOM) {
                        activateEvent(TransferType.PUT_SELF_OTHER, cursorItem, bottom, top, event, action);
                    }
                    break;
                }
                case HOTBAR_SWAP: {
                    if (inventoryIsPlayer) {
                        if (!crafting || getLocOfView(event.getView(), event.getRawSlot()) == InventoryLoc.BOTTOM) {
                            break;
                        }
                        if (player.getInventory().getItem(event.getHotbarButton()) == null) {
                            activateEvent(TransferType.PUT_CRAFTING_SELF, currentItem, top, bottom, event, action);
                            break;
                        }
                        activateEvent(TransferType.PUT_SELF_CRAFTING, player.getInventory().getItem(event.getHotbarButton()),
                                bottom, top, event, action);
                        break;
                    }
                    if (player.getInventory().getItem(event.getHotbarButton()) == null) {
                        activateEvent(TransferType.PUT_OTHER_SELF, currentItem, top, bottom, event, action);
                        break;
                    }
                    activateEvent(TransferType.PUT_SELF_OTHER, player.getInventory().getItem(event.getHotbarButton()),
                            bottom, top, event, action);
                    break;
                }
                case COLLECT_TO_CURSOR: {
                    final ItemStack selectedItem = event.getCursor();
                    int amount = selectedItem.getAmount();
                    List<ItemStack> topItems = new ArrayList<>();
                    List<ItemStack> bottomItems = new ArrayList<>();
                    for (int i = 0; i < (top.getSize() + bottom.getSize()); i++) {
                        if (amount >= selectedItem.getMaxStackSize()) {
                            break;
                        }
                        final ItemStack item = event.getView().getItem(i);
                        if (item == null) {
                            continue;
                        }
                        if (!selectedItem.isSimilar(item)) {
                            continue;
                        }
                        amount = amount + item.getAmount();
                        if (i < top.getSize()) {
                            if (pickedUpLoc == InventoryLoc.TOP) {
                                continue;
                            }
                            topItems.add(item);
                            continue;
                        }
                        if (pickedUpLoc == InventoryLoc.BOTTOM) {
                            continue;
                        }
                        bottomItems.add(item);
                    }
                    if (!topItems.isEmpty()) {
                        activateEvent(TransferType.PUT_OTHER_SELF, bottomItems, top, bottom, event, action);
                    }
                    if (!bottomItems.isEmpty()) {
                        activateEvent(TransferType.PUT_SELF_OTHER, topItems, bottom, top, event, action);
                    }
                }
                case MOVE_TO_OTHER_INVENTORY: {
                    if (!inventoryIsPlayer) {
                        activateEvent(TransferType.PUT_OTHER_SELF, currentItem, top, bottom, event, action);
                    } else if (!crafting) {
                        activateEvent(TransferType.PUT_SELF_OTHER, currentItem, bottom, top, event, action);
                    } else if (clickedIsCrafting) {
                        activateEvent(TransferType.PUT_CRAFTING_SELF, currentItem, top, bottom, event, action);
                    }
                }
            }
        }
        if (event.isCancelled()) {
            return;
        }
        if (put) {
            pickedItems.put(player.getUniqueId(), new PickedItems(clickedInvLoc, currentItem.clone(), event.getRawSlot(), event.getView()));
        }
        if (remove) {
            pickedItems.remove(player.getUniqueId());
        }
    }

    private boolean activateEvent(@SuppressWarnings("SameParameterValue") final TransferType type, final ItemStack item,
                                  final Inventory from, final InventoryView view) {
        return activateEvent(type, Collections.singletonList(item), from, null, view, null, null);
    }

    private void activateEvent(final TransferType type, final List<ItemStack> items, final Inventory from, final Inventory to,
                               final InventoryDragEvent event) {
        activateEvent(type, items, from, to, event.getView(), event, null);
    }

    private void activateEvent(final TransferType type, final ItemStack item, final Inventory from, final Inventory to,
                               final InventoryClickEvent event, final InventoryAction specificType) {
        activateEvent(type, Collections.singletonList(item), from, to, event, specificType);
    }

    private void activateEvent(final TransferType type, final List<ItemStack> items, final Inventory from, final Inventory to,
                               final InventoryClickEvent event, final InventoryAction specificType) {
        activateEvent(type, items, from, to, event.getView(), event, specificType);
    }

    private boolean activateEvent(final TransferType type, final List<ItemStack> items, final Inventory from, final Inventory to,
                                  final InventoryView view, final Cancellable event, final InventoryAction specificType) {
        final boolean crafting = view.getTopInventory().getType() == InventoryType.CRAFTING;
        final InventoryItemTransferEvent e = new InventoryItemTransferEvent(type.applyCrafting(crafting), view,
                Collections.unmodifiableList(items), from, to, event != null && event.isCancelled(), specificType);
        Bukkit.getPluginManager().callEvent(e);
        if (event != null) {
            event.setCancelled(e.isCancelled());
        }
        return e.isCancelled();
    }

    private InventoryLoc getLocOfView(InventoryView view, int rawSlot) {
        if (rawSlot < view.getTopInventory().getSize()) {
            return InventoryLoc.TOP;
        }
        return InventoryLoc.BOTTOM;
    }
}