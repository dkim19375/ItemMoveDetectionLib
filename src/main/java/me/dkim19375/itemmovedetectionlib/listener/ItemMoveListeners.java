package me.dkim19375.itemmovedetectionlib.listener;

import me.dkim19375.itemmovedetectionlib.event.InventoryItemTransferEvent;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class ItemMoveListeners implements Listener {
    private final Map<UUID, InventoryLoc> pickedItems = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    private void onDrag(InventoryDragEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = event.getView().getTopInventory();
        final Inventory bottom = event.getView().getBottomInventory();
        final InventoryLoc pickedUpLoc = pickedItems.getOrDefault(player.getUniqueId(), null);

        final List<ItemStack> topItems = new ArrayList<>();
        final List<ItemStack> bottomItems = new ArrayList<>();
        for (Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
            if (entry.getKey() < top.getSize()) {
                topItems.add(entry.getValue());
                continue;
            }
            bottomItems.add(entry.getValue());
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
        final InventoryLoc pickedUpLoc = pickedItems.getOrDefault(player.getUniqueId(), null);
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
                        null, event);
            }
            return;
        }
        if (simplifiedAction == SimplifiedAction.PICKUP && clickedInventory.getType() != InventoryType.CRAFTING) {
            put = true;
        }
        if (simplifiedAction == SimplifiedAction.DROP_CURSOR) {
            activateEvent(inventoryIsPlayer ? TransferType.DROP_SELF : TransferType.DROP_OTHER, cursorItem,
                    inventoryIsPlayer ? bottom : top,
                    null, event);
        }
        if (simplifiedAction == SimplifiedAction.DROP_SLOT) {
            activateEvent(inventoryIsPlayer ? TransferType.DROP_SELF : TransferType.DROP_OTHER, currentItem,
                    inventoryIsPlayer ? bottom : top,
                    null, event);
        }
        if (simplifiedAction == SimplifiedAction.PLACE) {
            remove = true;
            if (pickedUpLoc != null) {
                if (pickedUpLoc == InventoryLoc.TOP) {
                    if (inventoryIsPlayer) {
                        activateEvent(TransferType.PUT_OTHER, cursorItem, top, bottom, event);
                    }
                } else {
                    if (!inventoryIsPlayer) {
                        activateEvent(TransferType.PUT_SELF, cursorItem, bottom, top, event);
                    }
                }
            }
        }
        if (simplifiedAction == SimplifiedAction.SWAP_WITH_CURSOR) {
            if (pickedUpLoc != null) {
                if (!inventoryIsPlayer && pickedUpLoc == InventoryLoc.TOP) {
                    activateEvent(TransferType.PUT_OTHER, cursorItem, top, bottom, event);
                } else if (inventoryIsPlayer && pickedUpLoc == InventoryLoc.BOTTOM) {
                    activateEvent(TransferType.PUT_SELF, cursorItem, bottom, top, event);
                }
            }
            put = true;
        }
        if (simplifiedAction == SimplifiedAction.HOTBAR_SWAP) {
            if (!inventoryIsPlayer) {
                if (player.getInventory().getItem(event.getHotbarButton()) == null) {
                    activateEvent(TransferType.PUT_OTHER, currentItem, top, bottom, event);
                } else {
                    activateEvent(TransferType.PUT_SELF, player.getInventory().getItem(event.getHotbarButton()),
                            bottom, top, event);
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
                activateEvent(TransferType.PUT_OTHER, bottomItems, top, bottom, event);
                System.out.println("type: PUT_OTHER");
            }
            if (!bottomItems.isEmpty()) {
                activateEvent(TransferType.PUT_SELF, topItems, bottom, top, event);
                System.out.println("type: PUT_SELF");
            }
        }
        if (simplifiedAction == SimplifiedAction.MOVE_TO_OTHER_INVENTORY) {
            if (inventoryIsPlayer) {
                activateEvent(TransferType.PUT_SELF, currentItem, bottom, top, event);
            }
            if (!inventoryIsPlayer) {
                activateEvent(TransferType.PUT_OTHER, currentItem, top, bottom, event);
            }
        }
        if (event.isCancelled()) {
            return;
        }
        if (put) {
            pickedItems.put(player.getUniqueId(), clickedInvLoc);
        }
        if (remove) {
            pickedItems.remove(player.getUniqueId());
        }
    }

    private void activateEvent(@SuppressWarnings("SameParameterValue") TransferType type, List<ItemStack> items,
                               Inventory from, Inventory to, InventoryDragEvent event) {
        activateEvent(type, items, from, to, event.getView(), event);
    }

    private void activateEvent(TransferType type, ItemStack item, Inventory from, Inventory to, InventoryClickEvent event) {
        activateEvent(type, Collections.singletonList(item), from, to, event);
    }

    private void activateEvent(TransferType type, List<ItemStack> items, Inventory from, Inventory to, InventoryClickEvent event) {
        activateEvent(type, items, from, to, event.getView(), event);
    }

    private void activateEvent(TransferType type, List<ItemStack> items, Inventory from, Inventory to,
                               InventoryView view, Cancellable event) {
        final InventoryItemTransferEvent e = new InventoryItemTransferEvent(type, view,
                Collections.unmodifiableList(items), from, to, event.isCancelled());
        Bukkit.getPluginManager().callEvent(e);
        event.setCancelled(e.isCancelled());
    }

    @SuppressWarnings("unused")
    private enum InventoryLoc {
        TOP,
        BOTTOM
    }
}
