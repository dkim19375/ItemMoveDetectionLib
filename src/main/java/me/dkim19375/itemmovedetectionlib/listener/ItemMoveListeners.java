package me.dkim19375.itemmovedetectionlib.listener;

import me.dkim19375.itemmovedetectionlib.event.InventoryItemTransferEvent;
import me.dkim19375.itemmovedetectionlib.util.EntryImpl;
import me.dkim19375.itemmovedetectionlib.util.SimplifiedAction;
import me.dkim19375.itemmovedetectionlib.util.TransferType;
import org.bukkit.Bukkit;
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
    private final Map<UUID, Map.Entry<InventoryLoc, Map.Entry<ItemStack, Map.Entry<Integer, InventoryView>>>> pickedItems = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onDrop(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final Map.Entry<InventoryLoc, Map.Entry<ItemStack, Map.Entry<Integer, InventoryView>>> entry = pickedItems.get(player.getUniqueId());
        pickedItems.remove(player.getUniqueId());
        if (entry == null) {
            return;
        }
        final InventoryLoc pickedUpLoc = entry.getKey();
        final Map.Entry<ItemStack, Map.Entry<Integer, InventoryView>> itemEntry = entry.getValue();
        final ItemStack item = itemEntry.getKey();
        final int rawSlot = itemEntry.getValue().getKey();
        final InventoryView view = itemEntry.getValue().getValue();
        final Inventory top = view.getTopInventory();
        final Inventory bottom = view.getBottomInventory();

        final boolean cancelled = activateEvent((pickedUpLoc == InventoryLoc.BOTTOM) ? TransferType.DROP_SELF : TransferType.DROP_OTHER, item,
                (pickedUpLoc == InventoryLoc.BOTTOM) ? bottom : top, view);
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
        final Map.Entry<InventoryLoc, Map.Entry<ItemStack, Map.Entry<Integer, InventoryView>>> entry = pickedItems.getOrDefault(player.getUniqueId(), null);
        final InventoryLoc pickedUpLoc = entry == null ? null : entry.getKey();

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
            activateEvent(TransferType.PUT_OTHER, bottomItems, top, bottom, event);
        }
        if (pickedUpLoc == InventoryLoc.BOTTOM && (!topItems.isEmpty())) {
            activateEvent(TransferType.PUT_SELF, bottomItems, bottom, top, event);
        }
        if (!event.isCancelled()) {
            pickedItems.remove(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Map.Entry<InventoryLoc, Map.Entry<ItemStack, Map.Entry<Integer, InventoryView>>> entry = pickedItems.getOrDefault(player.getUniqueId(), null);
        final InventoryLoc pickedUpLoc = entry == null ? null : entry.getKey();
        final Inventory top = event.getView().getTopInventory();
        final Inventory bottom = event.getView().getBottomInventory();
        final Inventory clickedInventory = event.getClickedInventory();
        final InventoryAction action = event.getAction();
        final SimplifiedAction simplifiedAction = SimplifiedAction.fromAction(action);
        final ItemStack currentItem = event.getCurrentItem();
        final ItemStack cursorItem = event.getCursor();
        final boolean inventoryIsPlayer = clickedInventory != null && (clickedInventory instanceof PlayerInventory ||
                clickedInventory.getType() == InventoryType.CRAFTING);
        final InventoryLoc clickedInvLoc = inventoryIsPlayer ? InventoryLoc.BOTTOM : InventoryLoc.TOP;
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
        if (simplifiedAction == SimplifiedAction.PICKUP && clickedInventory.getType() != InventoryType.CRAFTING) {
            put = true;
        }
        if (simplifiedAction == SimplifiedAction.DROP_CURSOR) {
            activateEvent(inventoryIsPlayer ? TransferType.DROP_SELF : TransferType.DROP_OTHER, cursorItem,
                    inventoryIsPlayer ? bottom : top,
                    null, event, action);
        }
        if (simplifiedAction == SimplifiedAction.DROP_SLOT) {
            activateEvent(inventoryIsPlayer ? TransferType.DROP_SELF : TransferType.DROP_OTHER, currentItem,
                    inventoryIsPlayer ? bottom : top,
                    null, event, action);
        }
        if (simplifiedAction == SimplifiedAction.PLACE) {
            remove = true;
            if (pickedUpLoc != null) {
                if (pickedUpLoc == InventoryLoc.TOP) {
                    if (inventoryIsPlayer) {
                        activateEvent(TransferType.PUT_OTHER, cursorItem, top, bottom, event, action);
                    }
                } else {
                    if (!inventoryIsPlayer) {
                        activateEvent(TransferType.PUT_SELF, cursorItem, bottom, top, event, action);
                    }
                }
            }
        }
        if (simplifiedAction == SimplifiedAction.SWAP_WITH_CURSOR) {
            if (pickedUpLoc != null) {
                if (!inventoryIsPlayer && pickedUpLoc == InventoryLoc.TOP) {
                    activateEvent(TransferType.PUT_OTHER, cursorItem, top, bottom, event, action);
                } else if (inventoryIsPlayer && pickedUpLoc == InventoryLoc.BOTTOM) {
                    activateEvent(TransferType.PUT_SELF, cursorItem, bottom, top, event, action);
                }
            }
            put = true;
        }
        if (simplifiedAction == SimplifiedAction.HOTBAR_SWAP) {
            if (!inventoryIsPlayer) {
                if (player.getInventory().getItem(event.getHotbarButton()) == null) {
                    activateEvent(TransferType.PUT_OTHER, currentItem, top, bottom, event, action);
                } else {
                    activateEvent(TransferType.PUT_SELF, player.getInventory().getItem(event.getHotbarButton()),
                            bottom, top, event, action);
                }
            }
        }
        if (simplifiedAction == SimplifiedAction.COLLECT_TO_CURSOR) {
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
                activateEvent(TransferType.PUT_OTHER, bottomItems, top, bottom, event, action);
            }
            if (!bottomItems.isEmpty()) {
                activateEvent(TransferType.PUT_SELF, topItems, bottom, top, event, action);
            }
        }
        if (simplifiedAction == SimplifiedAction.MOVE_TO_OTHER_INVENTORY && !inventoryIsPlayer) {
            activateEvent(TransferType.PUT_OTHER, currentItem, top, bottom, event, action);
        }
        if (event.isCancelled()) {
            return;
        }
        if (put) {
            pickedItems.put(player.getUniqueId(),
                    new EntryImpl<>(clickedInvLoc,
                    new EntryImpl<>(currentItem.clone(),
                    new EntryImpl<>(event.getRawSlot(), event.getView()))));
        }
        if (remove) {
            pickedItems.remove(player.getUniqueId());
        }
    }

    private boolean activateEvent(@SuppressWarnings("SameParameterValue") TransferType type, ItemStack item,
                                  Inventory from, InventoryView view) {
        return activateEvent(type, Collections.singletonList(item), from, null, view, null, null);
    }

    private void activateEvent(TransferType type, List<ItemStack> items, Inventory from, Inventory to, InventoryDragEvent event) {
        activateEvent(type, items, from, to, event.getView(), event, null);
    }

    private void activateEvent(TransferType type, ItemStack item, Inventory from, Inventory to, InventoryClickEvent event, InventoryAction specificType) {
        activateEvent(type, Collections.singletonList(item), from, to, event, specificType);
    }

    private void activateEvent(TransferType type, List<ItemStack> items, Inventory from, Inventory to, InventoryClickEvent event, InventoryAction specificType) {
        activateEvent(type, items, from, to, event.getView(), event, specificType);
    }

    private boolean activateEvent(TransferType type, List<ItemStack> items, Inventory from, Inventory to,
                                  InventoryView view, Cancellable event, InventoryAction specificType) {
        final InventoryItemTransferEvent e = new InventoryItemTransferEvent(type, view,
                Collections.unmodifiableList(items), from, to, event != null && event.isCancelled(), specificType);
        Bukkit.getPluginManager().callEvent(e);
        if (event != null) {
            event.setCancelled(e.isCancelled());
        }
        return e.isCancelled();
    }

    @SuppressWarnings("unused")
    private enum InventoryLoc {
        TOP,
        BOTTOM
    }
}
